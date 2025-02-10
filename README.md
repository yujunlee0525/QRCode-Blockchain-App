# TheCloudCrew-F24
F24 15619 Cloud Computing: TheCloudCrew-F24

-----------------
# Team Members
Jialin Yu, Yujun Lee, Helom Berhane

-----------------
Step:
1. Create Cluster
```bash
chmod +x ./create_cluster.sh
```



2. Create new AWS instance using terraform
```bash
cd ../instance_create
#rm -rf .terraform
#rm terraform.tfstate terraform.tfstate.backup
terraform init
terraform apply --auto-approve
```


6. Deploy the service

```bash
kubectl create -f ./Ingress/ingress.yaml
helm install twitter ./helm/
# kubectl get services to find the External-IP
kubectl get services
```

3. When finish
```bash
helm uninstall twitter
kubectl delete -f ./Ingress/ingress.yaml
kops delete cluster cluster.k8s.local --yes
chmod +x ./delete-cluster.sh
./delete-cluster.sh
```
-----------------
# Update
1. build the docker image
```bash
# phase: 1, version: arm-1.0.0, service: blockchain
./image_build.sh -p phase -v version -s service
# or
export VERSION=arm-xxxx
export SERVICE=# recommendsys, blockchain, qrcode
export PHASE=3
cd blockChainApp
docker build -f ./docker/Dockerfile . -t $SERVICE:$VERSION
docker tag $SERVICE:$VERSION 390402562809.dkr.ecr.us-east-1.amazonaws.com/twitter-phase-$PHASE:$SERVICE-$VERSION
docker push 390402562809.dkr.ecr.us-east-1.amazonaws.com/twitter-phase-$PHASE:$SERVICE-$VERSION
```

-----------------
# ETL
Set up the environment value
```bash
export HTTP_CREDENTIAL=HTTP_PASSWORD
export SSH_CREDENTIALS=SSH_PASSWORD

export RESOURCE_GROUP_NAME=twitter-phase-3-spark-resources
export LOCATION=eastus
export CLUSTER_NAME=thecloudcrew-cc-spark-cluster
export AZURE_STORAGE_ACCOUNT="cloudcrewhdinsightpthree"
export AZURE_STORAGE_CONTAINER=$CLUSTER_NAME
export CLUSTER_VERSION=5.1
export CLUSTER_TYPE=spark
export COMPONENT_VERSION=Spark=3.3
export HEAD_NODE_SIZE='Standard_E4ads_V5'
export WORKER_NODE_COUNT=6
export TAGS='project=twitter-phase-3'
```

Create Resource
```bash
az provider register --namespace Microsoft.HDInsight --wait
az hdinsight list-usage --location eastus

az group create --location $LOCATION --name $RESOURCE_GROUP_NAME
az storage account create --name $AZURE_STORAGE_ACCOUNT --resource-group $RESOURCE_GROUP_NAME --https-only true --kind StorageV2 --location $LOCATION --sku Standard_LRS
export AZURE_STORAGE_KEY=$(az storage account keys list --account-name $AZURE_STORAGE_ACCOUNT --resource-group $RESOURCE_GROUP_NAME --query [0].value -o tsv)
az storage container create --name $AZURE_STORAGE_CONTAINER --account-key $AZURE_STORAGE_KEY --account-name $AZURE_STORAGE_ACCOUNT
az hdinsight create --name $CLUSTER_NAME --resource-group $RESOURCE_GROUP_NAME --type $CLUSTER_TYPE --component-version $COMPONENT_VERSION --http-password $HTTP_CREDENTIAL --http-user admin --location $LOCATION --ssh-password $SSH_CREDENTIALS --ssh-user azureuser --storage-account $AZURE_STORAGE_ACCOUNT --storage-account-key $AZURE_STORAGE_KEY --storage-container $AZURE_STORAGE_CONTAINER --version $CLUSTER_VERSION --headnode-size $HEAD_NODE_SIZE --workernode-count $WORKER_NODE_COUNT --workernode-size $HEAD_NODE_SIZE --zookeepernode-size $HEAD_NODE_SIZE --tags $TAGS
```
Delete Resource
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

-----------------
# Reference
For creating ingress file for the service:\
https://kubernetes-sigs.github.io/aws-load-balancer-controller/v2.2/guide/ingress/ingress_class/