#!/bin/sh
if [ -z "$VITE_BACKEND_URL" ]; then
  echo "Warning: VITE_BACKEND_URL is not set, frontend will use placeholder"
else
  echo "Injecting VITE_BACKEND_URL=$VITE_BACKEND_URL into JS"
  sed -i "s|__VITE_BACKEND_URL__|$VITE_BACKEND_URL|g" /usr/share/nginx/html/assets/*.js
fi

nginx -g "daemon off;"
