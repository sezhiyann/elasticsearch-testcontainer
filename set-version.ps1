clear
$CurrentProjectVersion = $( mvn help:evaluate -Dexpression="project.version" -q -DforceStdout )

Write-Host "Current project version: $CurrentProjectVersion"

$out = $( mvn build-helper:released-version help:evaluate -Dexpression='releasedVersion.version' -q -DforceStdout )
if ([string]::IsNullOrEmpty($out) -or $out -match "null object")
{
    Write-Host "No released version found."
    $ReleasedVersion = "0.0.0"
    $ReleasedIncrementalVersion = [int]"0"
    $ReleasedMinorVersion = [int]"0"
    $ReleasedMajorVersion = [int]"0"
}
else
{
    $ReleasedVersion = $out
    $ReleasedIncrementalVersion = [int]$( mvn build-helper:released-version help:evaluate -Dexpression='releasedVersion.incrementalVersion' -q -DforceStdout )
    $ReleasedMinorVersion = [int]$( mvn build-helper:released-version help:evaluate -Dexpression='releasedVersion.minorVersion' -q -DforceStdout )
    $ReleasedMajorVersion = [int]$( mvn build-helper:released-version help:evaluate -Dexpression='releasedVersion.majorVersion' -q -DforceStdout )
}

Write-Host "Released version: $ReleasedVersion"
Write-Host "Released major version: $ReleasedMajorVersion"
Write-Host "Released minor version: $ReleasedMinorVersion"
Write-Host "Released incremental version: $ReleasedIncrementalVersion"

$NextIncrementalVersion = [int]$( mvn build-helper:released-version build-helper:parse-version -DversionString="$ReleasedVersion" help:evaluate -Dexpression='formattedVersion.nextIncrementalVersion' -q -DforceStdout )
$NextMinorVersion = [int]$( mvn build-helper:released-version build-helper:parse-version -DversionString="$ReleasedVersion" help:evaluate -Dexpression='formattedVersion.nextMinorVersion' -q -DforceStdout )
$NextMajorVersion = [int]$( mvn build-helper:released-version build-helper:parse-version -DversionString="$ReleasedVersion" help:evaluate -Dexpression='formattedVersion.nextMajorVersion' -q -DforceStdout )

Write-Host "Next incremental version: $NextIncrementalVersion"
Write-Host "Next minor version: $NextMinorVersion"
Write-Host "Next major version: $NextMajorVersion"

$LastCommitMessage = $( git log -1 --pretty=%B )

Write-Host "Last commit message: $LastCommitMessage"

if ($LastCommitMessage -match "#version=incremental#")
{
    $NextVersion = "$ReleasedMajorVersion.$ReleasedMinorVersion.$NextIncrementalVersion"
}
elseif ($LastCommitMessage -match "#version=minor#")
{
    $NextVersion = "$ReleasedMajorVersion.$NextMinorVersion.0"
}
elseif ($LastCommitMessage -match "#version=major#")
{
    $NextVersion = "$NextMajorVersion.0.0"
}
else
{
    $NextVersion = "$ReleasedMajorVersion.$ReleasedMinorVersion.$NextIncrementalVersion"
}
Write-Host "Changed  version : $NextVersion"

$out = mvn versions:set versions:commit -DnewVersion="$NextVersion"



#properties available from build-helper-maven-plugin version 1.8
# [DEBUG] define property releasedVersion.version = "0.0.3"
# [DEBUG] define property releasedVersion.majorVersion = "0"
# [DEBUG] define property releasedVersion.minorVersion = "0"
# [DEBUG] define property releasedVersion.incrementalVersion = "3"
# [DEBUG] define property releasedVersion.buildNumber = "0"
# [DEBUG] define property releasedVersion.qualifier = ""
# [DEBUG] define property parsedVersion.majorVersion = "0"
# [DEBUG] define property parsedVersion.minorVersion = "0"
# [DEBUG] define property parsedVersion.incrementalVersion = "3"
# [DEBUG] define property parsedVersion.buildNumber = "0"
# [DEBUG] define property parsedVersion.nextMajorVersion = "1"
# [DEBUG] define property parsedVersion.nextMinorVersion = "1"
# [DEBUG] define property parsedVersion.nextIncrementalVersion = "4"
# [DEBUG] define property parsedVersion.nextBuildNumber = "1"
# [DEBUG] define property formattedVersion.majorVersion = "00"
# [DEBUG] define property formattedVersion.minorVersion = "00"
# [DEBUG] define property formattedVersion.incrementalVersion = "03"
# [DEBUG] define property formattedVersion.buildNumber = "00"
# [DEBUG] define property formattedVersion.nextMajorVersion = "01"
# [DEBUG] define property formattedVersion.nextMinorVersion = "01"
# [DEBUG] define property formattedVersion.nextIncrementalVersion = "04"
# [DEBUG] define property formattedVersion.nextBuildNumber = "01"
# [DEBUG] define property parsedVersion.qualifier = ""
# [DEBUG] define property parsedVersion.qualifier? = ""
# [DEBUG] define property parsedVersion.osgiVersion = "0.0.3"