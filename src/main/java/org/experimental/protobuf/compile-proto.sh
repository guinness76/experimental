#rm ./generated/*
./protoc-24.0-win64/bin/protoc.exe --proto_path=./protocols --java_out=../../../ ./protocols/*.proto
