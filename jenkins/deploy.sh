ssh -o StrictHostKeyChecking=no -l ${deploy_user} ${server} mkdir -p ${app_home}/jar
ssh -o StrictHostKeyChecking=no -l ${deploy_user} ${server} mkdir -p ${log_home}
