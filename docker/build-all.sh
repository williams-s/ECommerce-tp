#!/bin/bash
cd ../ms-membership
mvn clean package
docker build -t kabraass/ecommerce-membership:1.0 .

cd ../ms-products
mvn clean package
docker build -t kabraass/ecommerce-product:1.0 .

cd ../ms-orders
mvn clean package
docker build -t kabraass/ecommerce-order:1.0 .