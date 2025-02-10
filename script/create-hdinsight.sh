#!/bin/bash

# Function to prompt for credentials or use -c
get_credentials() {
        read -s -p "Enter the credentials (HTTP and SSH): " CREDENTIAL
        echo
        echo "Credentials set successfully."
}

# Get credentials
get_credentials "$1"

# Export environment variables
export HTTP_CREDENTIAL=$CREDENTIAL
export SSH_CREDENTIALS=$CREDENTIAL
export RESOURCE_GROUP_NAME="twitter-phase-3-spark-resources"
export LOCATION="eastus"
export CLUSTER_NAME="thecloudcrew-cc-spark-cluster"
export AZURE_STORAGE_ACCOUNT="cloudcrewhdinsightpthree"
export AZURE_STORAGE_CONTAINER=$CLUSTER_NAME
export CLUSTER_VERSION=5.1
export CLUSTER_TYPE="spark"
export COMPONENT_VERSION="Spark=3.3"
export HEAD_NODE_SIZE="Standard_E4ads_V5"
export WORKER_NODE_COUNT=5
export TAGS="project=twitter-phase-3"

# Display confirmation
echo "Environment variables have been set:"
echo "-----------------------------------"
echo "HTTP_CREDENTIAL: [HIDDEN]"
echo "SSH_CREDENTIALS: [HIDDEN]"
echo "RESOURCE_GROUP_NAME: $RESOURCE_GROUP_NAME"
echo "LOCATION: $LOCATION"
echo "CLUSTER_NAME: $CLUSTER_NAME"
echo "AZURE_STORAGE_ACCOUNT: $AZURE_STORAGE_ACCOUNT"
echo "AZURE_STORAGE_CONTAINER: $AZURE_STORAGE_CONTAINER"
echo "CLUSTER_VERSION: $CLUSTER_VERSION"
echo "CLUSTER_TYPE: $CLUSTER_TYPE"
echo "COMPONENT_VERSION: $COMPONENT_VERSION"
echo "HEAD_NODE_SIZE: $HEAD_NODE_SIZE"
echo "WORKER_NODE_COUNT: $WORKER_NODE_COUNT"
echo "TAGS: $TAGS"

az provider register --namespace Microsoft.HDInsight --wait
az hdinsight list-usage --location eastus

az group create --location $LOCATION --name $RESOURCE_GROUP_NAME
az storage account create --name $AZURE_STORAGE_ACCOUNT --resource-group $RESOURCE_GROUP_NAME --https-only true --kind StorageV2 --location $LOCATION --sku Standard_LRS
export AZURE_STORAGE_KEY=$(az storage account keys list --account-name $AZURE_STORAGE_ACCOUNT --resource-group $RESOURCE_GROUP_NAME --query [0].value -o tsv)
az storage container create --name $AZURE_STORAGE_CONTAINER --account-key $AZURE_STORAGE_KEY --account-name $AZURE_STORAGE_ACCOUNT
az hdinsight create --name $CLUSTER_NAME --resource-group $RESOURCE_GROUP_NAME --type $CLUSTER_TYPE --component-version $COMPONENT_VERSION --http-password $HTTP_CREDENTIAL --http-user admin --location $LOCATION --ssh-password $SSH_CREDENTIALS --ssh-user azureuser --storage-account $AZURE_STORAGE_ACCOUNT --storage-account-key $AZURE_STORAGE_KEY --storage-container $AZURE_STORAGE_CONTAINER --version $CLUSTER_VERSION --headnode-size $HEAD_NODE_SIZE --workernode-count $WORKER_NODE_COUNT --workernode-size $HEAD_NODE_SIZE --zookeepernode-size $HEAD_NODE_SIZE --tags $TAGS
