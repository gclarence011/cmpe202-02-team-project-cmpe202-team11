#!/bin/sh
set -e
cat /env.template.js | envsubst '$API_BASE_URL' > /usr/share/nginx/html/env.js
exec nginx -g 'daemon off;'
