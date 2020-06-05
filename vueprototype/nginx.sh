#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
docker run --rm --name ng -v $DIR/nginx:/etc/nginx:ro -v $DIR/dist:/usr/share/nginx/html:ro -p 80:80 nginx
