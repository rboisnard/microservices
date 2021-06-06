#syntax=docker/dockerfile:1.2

ARG arch=default
ARG version=latest
FROM usvc_runtime/${arch}:${version}

RUN    apk update  \
    && apk add     \
        build-base \
        clang      \
        cmake