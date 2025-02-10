#helm uninstall blockchain
#kubectl delete -f ./Ingress/ingress.yaml
export KOPES_STATE_STORE="s3://kops-state-store-pzcstxzw"
kops delete cluster cluster.k8s.local --yes

cd state-store
terraform destroy --auto-approve
cd ../instance-store
terraform destroy --auto-approve