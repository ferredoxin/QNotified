#!/usr/bin/env bash

commit=$(git show -s --format="%s %b")
echo "commit: $commit"
echo "trigger: $APPCENTER_TRIGGER"

if [[ $APPCENTER_TRIGGER == "manual" ]]; then
    echo "Manual trigger, continue building..."
    exit 0
fi

if [[ $commit == *"[skip ci]"* ]]; then
    curl -i -X PATCH -H "X-API-Token:$APPCENTER_API_TOKEN" -H "Content-Type: application/json" -d "{\"status\":\"cancelling\"}" https://appcenter.ms/api/v0.1/apps/QNotifiedDev/QNotified/builds/$APPCENTER_BUILD_ID
else
    echo "Continue building..."
fi