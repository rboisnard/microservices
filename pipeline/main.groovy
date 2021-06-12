def build_images(String composefile) {
  echo "create image to use docker-compose"
  def docker_id = sh(
    script: "getent group docker | awk -F: '{print \$3}'",
    returnStdout: true
  ).trim()

  def compose = docker.build("usvc_compose",
      "-f images/Dockerfile.compose images/"
    + " --build-arg docker_id=${docker_id}")

  compose.inside("-v /var/run/docker.sock:/var/run/docker.sock --group-add docker") {
    sh """
      ls -l /var/run/docker.sock
      ls -l /var/jenkins_home/workspace/
      ls -l /var/jenkins_home/workspace/*
      docker-compose -f images/${composefile} -p usvc build
    """
  }
}

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
    // TODO: run in different nodes for arm and x86
    stash name: "repo"
    build_images("docker-compose.armv7.yaml")
    node("x86_slave") {
      unstash "repo"
      build_images("docker-compose.x86_64.yaml")
    }
  }
}
return this