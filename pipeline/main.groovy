def call() {
  def build_version = "unknown"

  stage("version") {
    // TODO: implement build versions
    if (is_pull_request()) {
      build_version = "PR" + env.CHANGE_ID + "." + env.BUILD_NUMBER
    }
    else {
      build_version = env.BRANCH_NAME + "." + env.BUILD_NUMBER
    }
    echo "version is ${build_version}"
  }

  stage("images") {
    stash name: "repo"

    def image_nodes = [:]

    image_nodes["x86"] = {
      node("x86_slave") {
        unstash "repo"
        sh "docker-compose -f images/docker-compose.x86_64.yaml -p usvc build"
      }
    }
    image_nodes["arm"] = {
      node("arm_slave") {
        unstash "repo"
        sh "docker-compose -f images/docker-compose.aarch64.yaml -p usvc build"
      }
    }

    parallel(image_nodes)
  }
}
return this