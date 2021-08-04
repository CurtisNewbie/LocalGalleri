#!/bin/bash

(cd front/gallerifront/; ng build)

cp -r front/gallerifront/dist/gallerifront/* back/galleriback/src/main/resources/static/

mvn clean package -f back/galleriback/
