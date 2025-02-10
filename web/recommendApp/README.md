
```bash
az group create --location $LOCATION --name $RESOURCE_GROUP_NAME

az storage account create --name $AZURE_STORAGE_ACCOUNT --resource-group $RESOURCE_GROUP_NAME --https-only true --kind StorageV2 --location $LOCATION --sku Standard_LRS

export AZURE_STORAGE_KEY=$(az storage account keys list --account-name $AZURE_STORAGE_ACCOUNT --resource-group $RESOURCE_GROUP_NAME --query [0].value -o tsv)

az storage container create --name $AZURE_STORAGE_CONTAINER --account-key $AZURE_STORAGE_KEY --account-name $AZURE_STORAGE_ACCOUNT

az hdinsight create --name $CLUSTER_NAME --resource-group $RESOURCE_GROUP_NAME --type $CLUSTER_TYPE --component-version $COMPONENT_VERSION --http-password $HTTP_CREDENTIAL --http-user admin --location $LOCATION --ssh-password $SSH_CREDENTIALS --ssh-user azureuser --storage-account $AZURE_STORAGE_ACCOUNT --storage-account-key $AZURE_STORAGE_KEY --storage-container $AZURE_STORAGE_CONTAINER --version $CLUSTER_VERSION --headnode-size $HEAD_NODE_SIZE --workernode-count $WORKER_NODE_COUNT --workernode-size $HEAD_NODE_SIZE --zookeepernode-size $HEAD_NODE_SIZE --tags $TAGS

```

### Delete the resources

```bash
# Remove cluster
az hdinsight delete \
    --name $CLUSTER_NAME \
    --resource-group $RESOURCE_GROUP_NAME

# Remove storage container
az storage container delete \
    --account-name $AZURE_STORAGE_ACCOUNT \
    --name $AZURE_STORAGE_CONTAINER

# Remove storage account
az storage account delete \
    --name $AZURE_STORAGE_ACCOUNT \
    --resource-group $RESOURCE_GROUP_NAME

# Remove resource group
az group delete \
    --name $RESOURCE_GROUP_NAME
```
