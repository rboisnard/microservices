#syntax=docker/dockerfile:1.2

ARG arch=default
ARG version=latest
FROM usvc_base/${arch}:${version}

RUN    apk update  \
    && apk add     \
        ansible