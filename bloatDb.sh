#!/bin/bash


for i in $(seq 24 2000)
do
  echo "insert into Hotel (id, price, name, address, city, state, zip, country) values ($i, $RANDOM, '$(uuidgen)', '$(uuidgen)', '$(uuidgen)', '$(uuidgen)', '$(uuidgen)', 'USA')";
done