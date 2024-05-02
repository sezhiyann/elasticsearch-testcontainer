#!/bin/bash
clear
current_project_version=$(mvn help:evaluate -Dexpression="project.version" -q -DforceStdout)

echo "Current project version: $current_project_version"

out=$(mvn build-helper:released-version help:evaluate -Dexpression='releasedVersion.version' -q -DforceStdout)

if [[ -z "$out" ]] || [[ "$out" =~ "null object" ]]; then
  echo "No released version found."
  released_version="0.0.0"
  released_incremental_version="0"
  released_minor_version="0"
  released_major_version="0"
else
  released_version=$out
  released_incremental_version=$((10#$(mvn build-helper:released-version help:evaluate -Dexpression='releasedVersion.incrementalVersion' -q -DforceStdout)))
  released_minor_version=$((10#$(mvn build-helper:released-version help:evaluate -Dexpression='releasedVersion.minorVersion' -q -DforceStdout)))
  released_major_version=$((10#$(mvn build-helper:released-version help:evaluate -Dexpression='releasedVersion.majorVersion' -q -DforceStdout)))
fi

echo "Released version: $released_version"
echo "Released major version: $released_incremental_version"
echo "Released minor version: $released_minor_version"
echo "Released incremental version: $released_major_version"

next_incremental_version=$((10#$(mvn build-helper:released-version build-helper:parse-version -DversionString="$released_version" help:evaluate -Dexpression='formattedVersion.nextIncrementalVersion' -q -DforceStdout)))
next_minor_version=$((10#$(mvn build-helper:released-version build-helper:parse-version -DversionString="$released_version" help:evaluate -Dexpression='formattedVersion.nextMinorVersion' -q -DforceStdout)))
next_major_version=$((10#$(mvn build-helper:released-version build-helper:parse-version -DversionString="$released_version" help:evaluate -Dexpression='formattedVersion.nextMajorVersion' -q -DforceStdout)))

echo "Next incremental version: $next_incremental_version"
echo "Next minor version: $next_minor_version"
echo "Next major version: $next_major_version"

last_commit_message=$(git log -1 --pretty=%B)

echo "Last commit message: $last_commit_message"

if [[ "$last_commit_message" =~ "#version=incremental#" ]]; then
    next_version="$released_major_version.$released_minor_version.$next_incremental_version"
elif [[ $last_commit_message =~ "#version=minor#" ]]; then
    next_version="$released_major_version.$next_minor_version.0"
elif [[ $last_commit_message =~ "#version=major#" ]]; then
    next_version="$next_major_version.0.0"
else
    next_version="$released_major_version.$released_minor_version.$next_incremental_version"
fi
echo "Changed  version : $next_version"

mvn versions:set versions:commit -DnewVersion="$next_version"


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