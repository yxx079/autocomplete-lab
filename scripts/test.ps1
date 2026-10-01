$ErrorActionPreference = "Stop"
$ProjectDir = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$Classes = Join-Path $ProjectDir "target/classes"
$TestClasses = Join-Path $ProjectDir "target/test-classes"
New-Item -ItemType Directory -Force -Path $Classes, $TestClasses | Out-Null

$MainSources = Get-ChildItem -Recurse (Join-Path $ProjectDir "src/main/java") -Filter *.java | ForEach-Object FullName
$TestSources = Get-ChildItem -Recurse (Join-Path $ProjectDir "src/test/java") -Filter *.java | ForEach-Object FullName
$JUnit = Join-Path $ProjectDir "scripts/lib/junit-platform-console-standalone-6.0.0.jar"
$Runtime = @($Classes, $TestClasses) -join ";"

javac -encoding UTF-8 -d $Classes $MainSources
javac -encoding UTF-8 -cp "$Classes;$JUnit" -d $TestClasses $TestSources
java -jar $JUnit execute --class-path $Runtime --scan-class-path --details=summary
