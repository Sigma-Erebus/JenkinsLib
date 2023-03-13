import pycurl
import certifi
import jwt
import re
import os
import sys
import time
import json
from io import BytesIO
from google.oauth2 import service_account


# Google Authorization file for service account (json format)
AUTHFILE = sys.argv[1]

# File to be uploaded
# e.g. 'D:/Files/Downloads/pycurl-7.45.1-cp311-cp311-win_amd64.whl'
SOURCEFILE = sys.argv[2]

# Name of file once uploaded
# e.g. 'pycurl-7.45.1-cp311-cp311-win_amd64.whl'
FILENAME = sys.argv[3]

# ID of the parent folder the file should be uploaded to
# Is the final part of the URL in GDrive
GDPARENTID = sys.argv[4]

try:
    CHUNKSIZEMULT = int(sys.argv[5])
except:
    CHUNKSIZEMULT = 8



# open the file (r=read b=binary)
file = open(SOURCEFILE, "rb")
file_stats = os.stat(SOURCEFILE)

# define scopes to access using GDrive API
SCOPES = ['https://www.googleapis.com/auth/drive']


# Construct JWT for OAuth2.0 flow
AuthToken = service_account.Credentials.from_service_account_file(AUTHFILE, scopes = SCOPES)
#print(AuthToken)

# open the json file containing client data and private key
auth = open(AUTHFILE, "r").read()
iat = time.time()
exp = iat + 3600
iss = json.loads(auth)['client_email']
payload = {'iss': iss,
           'scope': 'https://www.googleapis.com/auth/drive',
           'aud': 'https://oauth2.googleapis.com/token',
           'iat': iat,
           'exp': exp}
signed_jwt = jwt.encode(payload, json.loads(auth)['private_key'], algorithm='RS256')
#print(signed_jwt)


# storage locations for return headers and body contents
buffer = BytesIO()
headers = {}
encoding = ""


uploadURL = ""

def header_function(header_line):
    # HTTP standard specifies that headers are encoded in iso-8859-1.
    # On Python 2, decoding step can be skipped.
    # On Python 3, decoding step is required.
    header_line = header_line.decode('iso-8859-1')

    # Header lines include the first status line (HTTP/1.x ...).
    # We are going to ignore all lines that don't have a colon in them.
    # This will botch headers that are split on multiple lines...
    if ':' not in header_line:
        return

    # Break the header line into header name and value.
    name, value = header_line.split(':', 1)

    # Remove whitespace that may be present.
    # Header lines include the trailing newline, and there may be whitespace
    # around the colon.
    name = name.strip()
    value = value.strip()

    # Header names are case insensitive.
    # Lowercase name here.
    name = name.lower()

    # Now we can actually record the header name and value.
    # Note: this only works when headers are not duplicated, see below.
    if name in headers:
        if isinstance(headers[name], list):
            headers[name].append(value)
        else:
            headers[name] = [headers[name], value]
    else:
        headers[name] = value


# retrieve access token from google's OAuth2.0
def retrieve_token():
    global headers
    global buffer
    global encoding
    headers = {}
    buffer = BytesIO()
    c = pycurl.Curl()
    #c.setopt(c.VERBOSE, True)
    c.setopt(c.CAINFO, certifi.where())
    c.setopt(c.URL, 'https://oauth2.googleapis.com/token')
    c.setopt(c.HTTPHEADER, ["Content-Type: application/x-www-form-urlencoded"])
    # use the JWT to authorize retrieval of a bearer token
    c.setopt(c.POSTFIELDS, 'grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&assertion=' + signed_jwt)
    c.setopt(c.WRITEFUNCTION, buffer.write)
    c.setopt(c.HEADERFUNCTION, header_function)
    c.perform()
    c.close()
    encoding = get_encoding()
    #print(buffer.getvalue().decode(encoding))
    return re.search('access_token":(\S+")', buffer.getvalue().decode(encoding)).group(1).replace('"', '').split(",")[0].replace('"', '')

# request creation of upload session and define uploadURL
def init_session():
    global headers
    global buffer
    global encoding
    global uploadURL
    c = pycurl.Curl()
    #c.setopt(c.VERBOSE, True)
    c.setopt(c.CAINFO, certifi.where())
    # request resumable upload with support for all types of drives
    c.setopt(c.URL, 'https://www.googleapis.com/upload/drive/v3/files?uploadType=resumable&supportsAllDrives=true')
    c.setopt(c.HTTPHEADER, ["Content-type: application/json; charset=UTF-8", "Authorization: Bearer {}".format(retrieve_token())])
    headers = {}
    buffer = BytesIO()
    c.setopt(c.POSTFIELDS, '{"name": "' + FILENAME + '", "parents": ["' + GDPARENTID + '"]}')
    c.setopt(c.WRITEFUNCTION, buffer.write)
    c.setopt(c.HEADERFUNCTION, header_function)
    c.perform()
    c.close()
    encoding = get_encoding()
    #print(buffer.getvalue().decode(encoding))
    uploadURL =  headers['location']

# send file data in chunks with a maximum length (multiple of 256 KB, {256 * 1024 * x})
def send_data(uploadURL, MaxLength, Start, Size):
    global headers
    global buffer
    global encoding
    c = pycurl.Curl()
    #c.setopt(c.VERBOSE, True)
    c.setopt(c.CAINFO, certifi.where())
    c.setopt(c.URL, uploadURL)
    temp = 0
    temp2 = 0
    # handle end of file
    if Start+MaxLength > Size:
        temp = Size-1
        temp2 = Size-Start
    else:
        temp = Start+MaxLength-1
        temp2 = MaxLength
    c.setopt(c.HTTPHEADER, ["Content-Length: {}".format(temp2), "Content-Range: bytes {}-{}/{}".format(Start, temp, Size), "Authorization: Bearer {}".format(retrieve_token())])
    headers = {}
    buffer = BytesIO()
    c.setopt(c.CUSTOMREQUEST, "PUT")
    # seek to required bytes instead of continuing from last request
    file.seek(Start, 0)
    chunk = file.read(MaxLength)
    c.setopt(c.POSTFIELDS, chunk)
    c.setopt(c.WRITEFUNCTION, buffer.write)
    c.setopt(c.HEADERFUNCTION, header_function)
    c.perform()
    c.close()
    print("Progress: {}%".format(round((temp+1)/Size*100, 1)))
    #print("Uploading byte range: {}-{}/{}".format(Start, temp, Size))
    encoding = get_encoding()
    #print(buffer.getvalue().decode(encoding))
    try:
        nextrange = headers['range'].split("=")[1].split("-")[1]
    except:
        nextrange = -1
    #print(nextrange)
    return nextrange

def get_encoding():
    encoding = None
    if 'content-type' in headers:
        content_type = headers['content-type'].lower()
        match = re.search('charset=(\S+)', content_type)
        if match:
            encoding = match.group(1)
            #print('Decoding using %s' % encoding)
    if encoding is None:
    # Default encoding for HTML is iso-8859-1.
    # Other content types may have different default encoding,
    # or in case of binary data, may have no encoding at all.
        encoding = 'iso-8859-1'
        print('Missing encoding, assuming encoding is %s' % encoding)
    return encoding



# Actual Uploading part

nextrange = 0

init_session()

while nextrange != -1:
    nextrange = send_data(uploadURL, 256*1024*CHUNKSIZEMULT, int(nextrange), file_stats.st_size)
    if nextrange != -1:
        #print(nextrange)
        nextrange = int(nextrange) + 1