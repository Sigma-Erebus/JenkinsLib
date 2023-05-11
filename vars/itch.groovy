def upload(butler, auth, source, target)
{
    bat(label: "Upload files to itch.io", script: "${butler} --identity=${auth} push ${source} ${target}")
}