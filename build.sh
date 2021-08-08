#!/bin/bash

(cd front/gallerifront/; ng build)

if [ ! -f back/galleriback/src/main/resources/static ]
then
    mkdir back/galleriback/src/main/resources/static
fi
cp -r front/gallerifront/dist/gallerifront/* back/galleriback/src/main/resources/static/

mvn clean package -f back/galleriback/
