# Jenkins Pushover Library

A Jenkins shared library for sending [Pushover](https://pushover.net/) notifications from your pipeline builds.

## Features

- Simple integration with Jenkins pipelines
- Customizable notification titles, priorities, and sounds
- Automatic inclusion of build URLs
- Non-blocking (won't fail your build if notification fails)

## Prerequisites

- Jenkins with Pipeline support
- `curl` installed on Jenkins agents
- Pushover account and application token
- Jenkins credentials configured (see Setup below)

## Installation

1. In Jenkins, go to **Manage Jenkins** → **Configure System**
2. Scroll to **Global Pipeline Libraries**
3. Add a new library with:
   - **Name**: `Pushover`
   - **Default version**: `main`
   - **Retrieval method**: Modern SCM
   - **Source Code Management**: Git
   - **Project Repository**: `https://github.com/rig0/jenkins-pushover`
4. Check **Load implicitly** if you want the library available to all pipelines

## Setup

Create two Jenkins credentials (Manage Jenkins → Credentials):

1. **Pushover User Key**:
   - Kind: Secret text
   - ID: `pushover-user-token`
   - Secret: Your Pushover user key (from https://pushover.net/)

2. **Pushover Application Token**:
   - Kind: Secret text
   - ID: `pushover-app-token`
   - Secret: Your application API token (create at https://pushover.net/apps/build)

## Usage

### Basic Usage

```groovy
@Library('Pushover') _

pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        // Your build steps
        echo 'Building...'
      }
    }
  }
  post {
    success {
      sendPushoverNotification('Build completed successfully!')
    }
    failure {
      sendPushoverNotification('Build failed!')
    }
  }
}
```

### Advanced Usage

```groovy
@Library('Pushover') _

pipeline {
  agent any
  stages {
    stage('Deploy') {
      steps {
        echo 'Deploying...'

        // Custom title and priority
        sendPushoverNotification(
          'Deployment started for production',
          'Production Deploy',
          0,
          'cosmic'
        )
      }
    }
  }
  post {
    success {
      // High priority success notification
      sendPushoverNotification(
        'Production deployment completed successfully!',
        'Deploy Success',
        1,
        'magic'
      )
    }
    failure {
      // Emergency priority failure notification
      sendPushoverNotification(
        'Production deployment FAILED! Immediate attention required.',
        'Deploy Failed',
        2,
        'siren'
      )
    }
  }
}
```

## Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `message` | String | *required* | The notification message to send |
| `title` | String | `'Jenkins'` | Notification title |
| `priority` | Integer | `0` | Priority level (see below) |
| `sound` | String | `'pushover'` | Notification sound (see below) |

### Priority Levels

- `-2`: Lowest priority (no notification/alert)
- `-1`: Low priority (no sound/vibration)
- `0`: Normal priority (default)
- `1`: High priority (bypass quiet hours)
- `2`: Emergency priority (requires acknowledgment)

### Available Sounds

Standard sounds: `pushover`, `bike`, `bugle`, `cashregister`, `classical`, `cosmic`, `falling`, `gamelan`, `incoming`, `intermission`, `magic`, `mechanical`, `pianobar`, `siren`, `spacealarm`, `tugboat`, `alien`, `climb`, `persistent`, `echo`, `updown`, `vibrate`, `none`

## Examples

### Notify on specific conditions

```groovy
stage('Test') {
  steps {
    script {
      def testResults = sh(returnStatus: true, script: 'npm test')
      if (testResults != 0) {
        sendPushoverNotification(
          'Tests are failing in branch ${env.BRANCH_NAME}',
          'Test Failure',
          1,
          'falling'
        )
      }
    }
  }
}
```

### Notify on long-running builds

```groovy
post {
  always {
    script {
      def duration = currentBuild.duration / 1000 / 60 // minutes
      if (duration > 30) {
        sendPushoverNotification(
          "Build took ${duration} minutes to complete",
          'Long Build Warning',
          0,
          'cosmic'
        )
      }
    }
  }
}
```

## License

MIT