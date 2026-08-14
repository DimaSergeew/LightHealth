#Requires -Version 7.0
<#
.SYNOPSIS
  Upload a LightHealth version to Hangar (PaperMC).

.NOTES
  Reads HANGAR_API_TOKEN (or HANGAR_API_KEY) from the environment.
  API key MUST include scopes: create_version (+ create_project recommended).
  Project is created once; this script only uploads versions.
#>
param(
    [string]$Version = "",
    [string]$Jar = "",
    [string]$Slug = "LightHealth",
    [string]$Channel = "Release"
)

$ErrorActionPreference = "Stop"
$ua = "LightHealth-Publish/1.0 (github.com/DimaSergeew/LightHealth)"
$root = Split-Path -Parent $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($Version)) {
    $versionLine = Get-Content (Join-Path $root "gradle.properties") |
        Where-Object { $_ -match '^version=' } |
        Select-Object -First 1
    if (-not $versionLine) {
        throw "Could not read version from gradle.properties"
    }
    $Version = ($versionLine -split '=', 2)[1].Trim()
}

function Get-HangarToken {
    if ($env:HANGAR_API_TOKEN) { return $env:HANGAR_API_TOKEN.Trim() }
    if ($env:HANGAR_API_KEY) { return $env:HANGAR_API_KEY.Trim() }
    throw "No Hangar token. Set HANGAR_API_TOKEN (or HANGAR_API_KEY)."
}

$token = Get-HangarToken
if (-not $Jar) {
    $Jar = Join-Path $root "build\libs\LightHealth-$Version.jar"
}
if (-not (Test-Path $Jar)) { throw "Jar not found: $Jar" }

$auth = Invoke-RestMethod -Uri "https://hangar.papermc.io/api/v1/authenticate?apiKey=$([uri]::EscapeDataString($token))" -Method POST -Headers @{ "User-Agent" = $ua }
$jwt = $auth.token
Write-Host "Authenticated as Hangar session (exp in JWT)."

$releaseNotesPath = Join-Path $root "RELEASE_NOTES.md"
$changelog = Get-Content -Raw -Encoding UTF8 $releaseNotesPath

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
Set the new key in HANGAR_API_TOKEN.
"@
    exit 1
}

Write-Host "Done: https://hangar.papermc.io/DimaSergeew/$Slug"
