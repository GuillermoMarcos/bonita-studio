#!/bin/sh

#
# args
# 1 : project api key
# 2 : path of the source to parse

if test ! $# -eq 2  
then
	echo "[ERROR] generateCrowdinYamlFile.sh scripts needs 2 parameters"
	exit
fi

projectAPIKey=$1
sourcePath=$2

crowdinFile=sourcePath/crowdin.yaml
crowdinTemplateFile=crowdin.yaml.template

echo "[INFO] Remove Crowdin configuration file if exists."
# remove crowdin config file if already exists
if test -f $crowdinFile
then
	echo " remove file $crowdinFile"
	rm $crowdinFile
fi

echo "[INFO] Create Crowdin configuration file."
# copy template
cp $crowdinTemplateFile $crowdinFile

# Set config
sed -i "s#@PROJECT_API_KEY@#$projectAPIKey#g" $crowdinFile
sed -i "s#@PATH_ON_CI@#$sourcePath#g"         $crowdinFile


