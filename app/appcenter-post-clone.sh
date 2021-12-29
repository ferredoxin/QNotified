#!/usr/bin/env bash

#
# QNotified - An Xposed module for QQ/TIM
# Copyright (C) 2019-2022 dmca@ioctl.cc
# https://github.com/ferredoxin/QNotified
#
# This software is non-free but opensource software: you can redistribute it
# and/or modify it under the terms of the GNU Affero General Public License
# as published by the Free Software Foundation; either
# version 3 of the License, or any later version and our eula as published
# by ferredoxin.
#
# This software is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# and eula along with this software.  If not, see
# <https://www.gnu.org/licenses/>
# <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
#

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
