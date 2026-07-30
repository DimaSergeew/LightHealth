#Requires -Version 7.0
<#
.SYNOPSIS
  Create or update the LightHealth Modrinth project and upload a version.

.NOTES
  Requires env MODRINTH_TOKEN (create at https://modrinth.com/settings/account ).
  Scopes: project:create, project:write, version:create, version:write (PAT with full project access).
#>
param(
    [string]$Version = "1.0.1",
    [string]$Jar = "",
    [string]$Slug = "lighthealth",
    [switch]$Draft
)

$ErrorActionPreference = "Stop"
$token = $env:MODRINTH_TOKEN
if ([string]::IsNullOrWhiteSpace($token)) {
    throw "Set MODRINTH_TOKEN environment variable first (Modrinth PAT)."
}

$root = Split-Path -Parent $PSScriptRoot
if (-not $Jar) {
    $Jar = Join-Path $root "build\libs\LightHealth-$Version.jar"
}
if (-not (Test-Path $Jar)) {
    throw "Jar not found: $Jar — run ./gradlew build first"
}

$bodyPath = Join-Path $root "modrinth\body.md"
$iconPath = Join-Path $root "assets\icon.png"
$galleryPath = Join-Path $root "assets\gallery.png"
$body = Get-Content -Raw -Encoding UTF8 $bodyPath

$headers = @{
    Authorization = $token
    "User-Agent"  = "LightHealth-Publish/1.0 (github.com/DimaSergeew/LightHealth)"
}

$api = "https://api.modrinth.com/v2"

function Invoke-Mr {
    param([string]$Method, [string]$Url, [hashtable]$Hdr, $Body, [string]$ContentType)
    $params = @{
        Method  = $Method
        Uri     = $Url
        Headers = $Hdr
    }
    if ($null -ne $Body) {
        if ($ContentType) {
            $params.ContentType = $ContentType
            $params.Body = $Body
        } else {
            $params.ContentType = "application/json"
            $params.Body = ($Body | ConvertTo-Json -Depth 20 -Compress)
        }
    }
    return Invoke-RestMethod @params
}

# --- ensure project ---
$project = $null
try {
    $project = Invoke-Mr -Method GET -Url "$api/project/$Slug" -Hdr $headers
    Write-Host "Project exists: $($project.id) ($Slug)"
} catch {
    Write-Host "Creating draft project '$Slug'..."
    $projectData = @{
        slug            = $Slug
        title           = "LightHealth"
        description     = "Modern mob health feedback for Paper / Folia: hologram, damage numbers, actionbar, bossbar, look-at."
        categories      = @("management", "utility")
        client_side     = "unsupported"
        server_side     = "required"
        body            = $body
        status          = "draft"
        project_type    = "mod"
        is_draft        = $true
        license_id      = "MIT"
        issues_url      = "https://github.com/DimaSergeew/LightHealth/issues"
        source_url      = "https://github.com/DimaSergeew/LightHealth"
        wiki_url        = "https://dimasergeew.github.io/LightHealth/"
        initial_versions = @()
    }

    # Multipart create
    $boundary = [guid]::NewGuid().ToString("N")
    $enc = [System.Text.Encoding]::UTF8
    $ms = New-Object System.IO.MemoryStream

    function Add-Part([System.IO.MemoryStream]$Stream, [string]$Name, [byte[]]$Content, [string]$FileName, [string]$Type) {
        $sb = New-Object System.Text.StringBuilder
        [void]$sb.AppendLine("--$boundary")
        if ($FileName) {
            [void]$sb.AppendLine("Content-Disposition: form-data; name=`"$Name`"; filename=`"$FileName`"")
            [void]$sb.AppendLine("Content-Type: $Type")
        } else {
            [void]$sb.AppendLine("Content-Disposition: form-data; name=`"$Name`"")
            [void]$sb.AppendLine("Content-Type: application/json")
        }
        [void]$sb.AppendLine()
        $headerBytes = $enc.GetBytes($sb.ToString())
        $Stream.Write($headerBytes, 0, $headerBytes.Length)
        $Stream.Write($Content, 0, $Content.Length)
        $nl = $enc.GetBytes("`r`n")
        $Stream.Write($nl, 0, $nl.Length)
    }

    $jsonBytes = $enc.GetBytes(($projectData | ConvertTo-Json -Depth 20 -Compress))
    Add-Part $ms "data" $jsonBytes $null $null
    if (Test-Path $iconPath) {
        $iconBytes = [System.IO.File]::ReadAllBytes($iconPath)
        Add-Part $ms "icon" $iconBytes "icon.png" "image/png"
    }
    $end = $enc.GetBytes("--$boundary--`r`n")
    $ms.Write($end, 0, $end.Length)

    $createHeaders = @{
        Authorization  = $token
        "User-Agent"   = $headers["User-Agent"]
        "Content-Type" = "multipart/form-data; boundary=$boundary"
    }
    $project = Invoke-RestMethod -Method POST -Uri "$api/project" -Headers $createHeaders -Body $ms.ToArray()
    Write-Host "Created project: $($project.id)"
}

$projectId = $project.id

# Sync body if project already existed
try {
    Invoke-Mr -Method PATCH -Url "$api/project/$projectId" -Hdr $headers -Body @{
        body        = $body
        description = "Modern mob health feedback for Paper / Folia: hologram, damage numbers, actionbar, bossbar, look-at."
        categories  = @("management", "utility")
        issues_url  = "https://github.com/DimaSergeew/LightHealth/issues"
        source_url  = "https://github.com/DimaSergeew/LightHealth"
        wiki_url    = "https://dimasergeew.github.io/LightHealth/"
        license_id  = "MIT"
    } | Out-Null
} catch {
    Write-Warning "Could not patch project metadata: $_"
}

# --- upload version ---
$changelog = @"
## $Version

Bugfix and reliability release for Paper / Folia.

### Fixes
- Folia: floating damage numbers no longer leave orphan TextDisplays on victim death
- hologram.only-when-damaged only affects holograms (not numbers/bars)
- /lh toggle + lighthealth.see unified (personal bars; holograms/numbers from your hits)
- Quit cleans prefs, actionbar generations, bossbars

### Improvements
- Holograms ride the mob (no per-tick teleport follow)
- Single raycast for look-at; cached Folia detection
- Locale files soft-merge new keys
- Removed empty bootstrapper

GitHub: https://github.com/DimaSergeew/LightHealth/releases/tag/v$Version
"@

$jarBytes = [System.IO.File]::ReadAllBytes($Jar)
$jarName = [System.IO.Path]::GetFileName($Jar)
$sha1 = (Get-FileHash -Path $Jar -Algorithm SHA1).Hash.ToLowerInvariant()
$sha512 = (Get-FileHash -Path $Jar -Algorithm SHA512).Hash.ToLowerInvariant()

$versionData = @{
    name           = "LightHealth $Version"
    version_number = $Version
    changelog      = $changelog
    dependencies   = @()
    game_versions  = @("1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11", "26.1", "26.1.1", "26.1.2", "26.2")
    version_type   = "release"
    loaders        = @("paper", "purpur", "folia")
    featured       = $true
    status         = if ($Draft) { "draft" } else { "listed" }
    project_id     = $projectId
    file_parts     = @("lighthealth")
    primary_file   = "lighthealth"
    file_types     = @{ lighthealth = $null }
}

$boundary = [guid]::NewGuid().ToString("N")
$enc = [System.Text.Encoding]::UTF8
$ms = New-Object System.IO.MemoryStream

function Add-TextPart([System.IO.MemoryStream]$Stream, [string]$Name, [string]$Text, [string]$Type = "application/json") {
    $header = "--$boundary`r`nContent-Disposition: form-data; name=`"$Name`"`r`nContent-Type: $Type`r`n`r`n"
    $hb = $enc.GetBytes($header)
    $Stream.Write($hb, 0, $hb.Length)
    $tb = $enc.GetBytes($Text)
    $Stream.Write($tb, 0, $tb.Length)
    $nl = $enc.GetBytes("`r`n")
    $Stream.Write($nl, 0, $nl.Length)
}

function Add-FilePart([System.IO.MemoryStream]$Stream, [string]$Name, [string]$FileName, [byte[]]$Content) {
    $header = "--$boundary`r`nContent-Disposition: form-data; name=`"$Name`"; filename=`"$FileName`"`r`nContent-Type: application/java-archive`r`n`r`n"
    $hb = $enc.GetBytes($header)
    $Stream.Write($hb, 0, $hb.Length)
    $Stream.Write($Content, 0, $Content.Length)
    $nl = $enc.GetBytes("`r`n")
    $Stream.Write($nl, 0, $nl.Length)
}

Add-TextPart $ms "data" ($versionData | ConvertTo-Json -Depth 20 -Compress)
Add-FilePart $ms "lighthealth" $jarName $jarBytes
$end = $enc.GetBytes("--$boundary--`r`n")
$ms.Write($end, 0, $end.Length)

$verHeaders = @{
    Authorization  = $token
    "User-Agent"   = $headers["User-Agent"]
    "Content-Type" = "multipart/form-data; boundary=$boundary"
}

Write-Host "Uploading version $Version (sha1=$sha1)..."
try {
    $ver = Invoke-RestMethod -Method POST -Uri "$api/version" -Headers $verHeaders -Body $ms.ToArray()
    Write-Host "Version uploaded: $($ver.id) — https://modrinth.com/plugin/$Slug/version/$($ver.id)"
} catch {
    $resp = $_.ErrorDetails.Message
    if ($resp -match "already exists" -or $resp -match "duplicate") {
        Write-Warning "Version may already exist: $resp"
    } else {
        throw "Version upload failed: $resp"
    }
}

# Request publish if draft project
if (-not $Draft -and $project.status -eq "draft") {
    try {
        Invoke-Mr -Method PATCH -Url "$api/project/$projectId" -Hdr $headers -Body @{
            requested_status = "approved"
        } | Out-Null
        Write-Host "Requested project approval (moderation queue)."
    } catch {
        Write-Warning "Could not request approval (submit manually on Modrinth): $_"
    }
}

Write-Host "Done. Project: https://modrinth.com/plugin/$Slug"
Write-Host "Note: new projects start as draft and need moderation approval before public listing."
