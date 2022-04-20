ssh root@ec2-18-130-217-147.eu-west-2.compute.amazonaws.com "rm -r /var/www/html/*"
scp -r build/* root@ec2-18-130-217-147.eu-west-2.compute.amazonaws.com:/var/www/html
