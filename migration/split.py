# Migration script
# This script splits csv file exported from the regnskaplog table
# and creates individual xml files that can be imported into another database
# See readme.md for description of use
# Remember to update filenames and paths in script if necessary
import pandas
import base64
import zipfile
from io import BytesIO
from zipfile import BadZipFile

regnskap = pandas.read_csv("dbdata/export.csv", sep=",", header=None)

for i,r in regnskap.iterrows():
    print("processing row " + str(i))
    filename = r[0]
    print("processing file " + str(i) + " name: " + filename)
    contents = r[1]
    b64_bytes = base64.b64decode(contents)
    filebytes = BytesIO(b64_bytes)
    try:
        zfile = zipfile.ZipFile(filebytes)
        zfile.extractall("output")
    except BadZipFile:
        print("   ERROR: File is not a zip file: " + filename)