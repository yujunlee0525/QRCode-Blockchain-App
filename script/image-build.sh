#!/bin/bash

# Initialize variables
SERVICE=""
VERSION=""
PHASE=""

# Parse command-line options
while getopts "s:v:p:" opt; do
  case $opt in
    s) SERVICE="$OPTARG"
    ;;
    v) VERSION="$OPTARG"
    ;;
    p) PHASE="$OPTARG"
    ;;
    \?) echo "Invalid option -$OPTARG" >&2
    exit 1
    ;;
  esac
done

# Check if all required arguments are provided
if [[ -z "$SERVICE" || -z "$VERSION" || -z "$PHASE" ]]; then
  echo "Usage: ./dockerupdate.sh -s <service> -v <version> -p <phase>"
  exit 1
fi

# Run the Docker commands
echo "Building Docker image..."
docker build -f ./docker/Dockerfile . -t $SERVICE:$VERSION

echo "Tagging Docker image..."
docker tag $SERVICE:$VERSION 390402562809.dkr.ecr.us-east-1.amazonaws.com/twitter-phase-$PHASE:$SERVICE-$VERSION

echo "Pushing Docker image to ECR..."
docker push 390402562809.dkr.ecr.us-east-1.amazonaws.com/twitter-phase-$PHASE:$SERVICE-$VERSION

echo "Docker image successfully built, tagged, and pushed."

