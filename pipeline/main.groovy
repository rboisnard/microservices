def call() {
  def build_version = "unknown"

  stage("version") {
    echo "TODO: implement build versions"
    
    if (is_pull_request()) {
      build_version = "PR" + env.CHANGE_ID + "." + env.BUILD_NUMBER
    }
    else {
      build_version = env.BRANCH_NAME + "." + env.BUILD_NUMBER
    }
    echo "version is ${build_version}"
  }

  stage("images") {
    def config = readYaml file: 'images/config.yml'
    echo "TODO: implement multi arch"
    for (image in config.images) {
      for (arch in image.archs) {
        if (arch == "armv7") {
          echo "build ${image.name}/${arch} version ${build_version}"
          sh """
            docker build                                \
              -t ${image.name}/${arch}:${build_version} \
              --build-arg version="${build_version}"    \
              --build-arg arch="${arch}"                \
              -f images/Dockerfile.${image.name}        \
              images/
          """
        }
      }
    }
  }
}
return this