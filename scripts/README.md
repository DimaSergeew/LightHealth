# Publishing

Publishing is intentionally manual. Build and inspect the jar first:

```powershell
./gradlew build
```

Both scripts read the version from `gradle.properties` and the changelog from
`RELEASE_NOTES.md` when `-Version` is omitted.

## Modrinth

```powershell
$env:MODRINTH_TOKEN = "your-token"
./scripts/publish-modrinth.ps1 -Draft
```

Remove `-Draft` only after checking the project body, supported game versions,
and generated `LightHealth-1.1.0.jar`.

## Hangar

```powershell
$env:HANGAR_API_TOKEN = "your-token"
./scripts/publish-hangar.ps1
```

Tokens are read only from environment variables and must never be committed.
