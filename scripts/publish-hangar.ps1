#Requires -Version 7.0
<#
.SYNOPSIS
  Upload a LightHealth version to Hangar (PaperMC).

.NOTES
  Reads HANGAR_API_TOKEN or falls back to ~/.grok/secrets/api-keys.md (Hangar section).
  API key MUST include scopes: create_version (+ create_project recommended).
  Project is created once; this script only uploads versions.
#>
param(
    [string]$Version = "1.0.1",
    [string]$Jar = "",
    [string]$Slug = "LightHealth",
    [string]$Channel = "Release"
)

$ErrorActionPreference = "Stop"
$ua = "LightHealth-Publish/1.0 (github.com/DimaSergeew/LightHealth)"

function Get-HangarToken {
    if ($env:HANGAR_API_TOKEN) { return $env:HANGAR_API_TOKEN.Trim() }
    if ($env:HANGAR_API_KEY) { return $env:HANGAR_API_KEY.Trim() }
    $secrets = Join-Path $env:USERPROFILE ".grok\secrets\api-keys.md"
    if (Test-Path $secrets) {
        $raw = Get-Content -Raw $secrets
        if ($raw -match 'API key:\s*`([^`]+)`') { return $Matches[1].Trim() }
    }
    throw "No Hangar token. Set HANGAR_API_TOKEN or update ~/.grok/secrets/api-keys.md"
}

$token = Get-HangarToken
$root = Split-Path -Parent $PSScriptRoot
if (-not $Jar) {
    $Jar = Join-Path $root "build\libs\LightHealth-$Version.jar"
}
if (-not (Test-Path $Jar)) { throw "Jar not found: $Jar" }

$auth = Invoke-RestMethod -Uri "https://hangar.papermc.io/api/v1/authenticate?apiKey=$([uri]::EscapeDataString($token))" -Method POST -Headers @{ "User-Agent" = $ua }
$jwt = $auth.token
Write-Host "Authenticated as Hangar session (exp in JWT)."

$changelog = @"
## $Version

Release for Spigot / Paper / Purpur / Folia.

GitHub: https://github.com/DimaSergeew/LightHealth/releases/tag/v$Version
Modrinth: https://modrinth.com/plugin/lighthealth
"@

$versionUpload = @{
    version               = $Version
    channel               = $Channel
    description           = $changelog
    platformDependencies  = @{
        PAPER = @("1.21.x", "26.1", "26.1.1", "26.1.2", "26.2")
    }
    files                 = @(
        @{ platforms = @("PAPER") }
    )
} | ConvertTo-Json -Depth 10 -Compress

$vuPath = Join-Path $env:TEMP "hangar-vu-$Version.json"
[System.IO.File]::WriteAllText($vuPath, $versionUpload, [Text.UTF8Encoding]::new($false))

Write-Host "Uploading $Jar → Hangar project $Slug ..."
$resp = curl.exe -sS -X POST "https://hangar.papermc.io/api/v1/projects/$Slug/upload" `
    -H "Authorization: HangarAuth $jwt" `
    -H "User-Agent: $ua" `
    -F "versionUpload=<$vuPath;type=application/json" `
    -F "files=@$Jar;type=application/java-archive" `
    -w "`nHTTP:%{http_code}"

Write-Host $resp
if ($resp -notmatch "HTTP:200") {
    Write-Host @"

If you see 404 Not Found: recreate the Hangar API key with scope **create_version**
(Hangar → Settings → API keys → tick create_version + create_project).
Update ~/.grok/secrets/api-keys.md with the new key.
"@
    exit 1
}

Write-Host "Done: https://hangar.papermc.io/DimaSergeew/$Slug"
