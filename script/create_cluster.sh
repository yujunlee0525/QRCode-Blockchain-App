cd state-store
terraform init
terraform apply --auto-approve
export KOPS_STATE_STORE="s3://$(terraform output -raw bucket_name)"
cd ../cluster-definition
kops create -f cluster.yaml,ig-master.yaml,ig-nodes.yaml
kops update cluster --name cluster.k8s.local --yes --admin=2400h
kops validate cluster --wait 10m
helm repo add eks https://aws.github.io/eks-charts
helm repo update
helm install aws-load-balancer-controller eks/aws-load-balancer-controller -n kube-system --set clusterName=cluster.k8s.local
aws iam attach-role-policy --role-name nodes.cluster.k8s.local --policy-arn arn:aws:iam::390402562809:policy/alb-controller-policy
helm repo add aws-ebs-csi-driver https://kubernetes-sigs.github.io/aws-ebs-csi-driver
helm repo update
kubectl apply -f https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/master/client/config/crd/snapshot.storage.k8s.io_volumesnapshotclasses.yaml
kubectl apply -f https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/master/client/config/crd/snapshot.storage.k8s.io_volumesnapshotcontents.yaml
kubectl apply -f https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/master/client/config/crd/snapshot.storage.k8s.io_volumesnapshots.yaml
kubectl apply -f https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/master/deploy/kubernetes/snapshot-controller/rbac-snapshot-controller.yaml
kubectl apply -f https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/master/deploy/kubernetes/snapshot-controller/setup-snapshot-controller.yaml
kubectl apply -f https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/master/deploy/kubernetes/csi-snapshotter/rbac-csi-snapshotter.yaml
kubectl apply -f https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/master/deploy/kubernetes/csi-snapshotter/rbac-external-provisioner.yaml
kubectl apply -f https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/master/deploy/kubernetes/csi-snapshotter/setup-csi-snapshotter.yaml
helm upgrade --install aws-ebs-csi-driver --namespace kube-system --set "controller.extraVolumeTags.Project=twitter-phase-2" aws-ebs-csi-driver/aws-ebs-csi-driver
aws iam attach-role-policy --role-name nodes.cluster.k8s.local --policy-arn arn:aws:iam::aws:policy/service-role/AmazonEBSCSIDriverPolicy