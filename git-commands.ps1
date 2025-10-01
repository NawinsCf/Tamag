# Script PowerShell d'aide pour initialiser et pousser le projet Tamago
# Remplace <REMOTE_URL> par l'URL du dépôt distant (ex: https://github.com/monorg/monrepo.git)

param(
    [string]$RemoteUrl = "<REMOTE_URL>"
)

Write-Host "Vérification de la présence de git..."
if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Host "git n'est pas installé ou pas dans le PATH. Installez Git for Windows : https://git-scm.com/download/win" -ForegroundColor Red
    exit 1
}

Write-Host "Configuration utilisateur git (optionnel)"
$cfgName = git config --global user.name
$cfgEmail = git config --global user.email
if (-not $cfgName) { git config --global user.name "Ton Nom"; Write-Host "user.name configuré par défaut (modifie si nécessaire)" }
if (-not $cfgEmail) { git config --global user.email "ton.email@example.com"; Write-Host "user.email configuré par défaut (modifie si nécessaire)" }

Write-Host "Initialisation du dépôt (si non initialisé) et premier push..."
cd "$PSScriptRoot"
if (-not (Test-Path .git)) {
    git init
    git add .
    git commit -m "Initial commit — Tamago project"
    git branch -M main
    if ($RemoteUrl -eq "<REMOTE_URL>") {
        Write-Host "Aucune URL remote fournie. Passe la paramètre -RemoteUrl 'https://...' pour pousser vers un remote." -ForegroundColor Yellow
        exit 0
    }
    git remote add origin $RemoteUrl
    git push -u origin main
} else {
    Write-Host "Le dépôt git existe déjà. Ajout des modifications et push..."
    git add -A
    git commit -m "Mise à jour: front + logos + README" -ErrorAction SilentlyContinue
    git push
}

Write-Host "Terminé." -ForegroundColor Green
