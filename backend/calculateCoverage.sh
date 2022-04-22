#!/bin/zsh
#cd "$( dirname "${BASH_SOURCE[0]}" )/../../backend"
cd ../
./gradlew :backend:jacocoTestReport -q
missed=0
covered=0
while read -r p; do
  missed=$((missed + $(echo "$p" | cut -d "," -f 4)))
  covered=$((covered + $(echo "$p" | cut -d "," -f 5)))
done <<< "$(grep "james.springboot.spring_game" backend/build/reports/jacoco/test/jacocoTestReport.csv)"
total=$((covered + missed))
percent=$((covered * 100 / total))
colour="brightgreen"
if ((percent < 50))
then
  colour="red"
elif ((percent < 60))
then
  colour="orange"
elif ((percent < 70))
then
  colour="yellow"
elif ((percent < 80))
then
  colour="yellowgreen"
elif ((percent < 90))
then
  colour="green"
elif ((percent < 100))
then
  colour="brightgreen"
else
  colour="blueviolet"
fi
echo \\\!\[https:\\/\\/img.shields.io\\/badge\\/Code_Coverage-"$percent"%25-"$colour"\]\(\)