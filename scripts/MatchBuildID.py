import sys
import glob
import json
import os, stat

# ProjectFolder
PROJECTDIR = sys.argv[1]

# EngineFolder
ENGINEDIR = sys.argv[2]

SourceBuildIdFile = glob.glob(pathname="Engine/Plugins/Animation/LiveLink/Binaries/Win64/UnrealEditor.modules", root_dir=ENGINEDIR)[0]
#print(ENGINEDIR+SourceBuildIdFile)
file = open(ENGINEDIR+SourceBuildIdFile, "rt")
data = json.load(file)
SourceBuildId = data['BuildId']
file.close()

TargetBuildIdFiles = glob.glob(pathname="Plugins/**/Binaries/Win64/UnrealEditor.modules", root_dir=PROJECTDIR, recursive=True)
for i in TargetBuildIdFiles:
    os.chmod(PROJECTDIR+i, stat.S_IWRITE)
    target = open(PROJECTDIR+i, "rt")
    data = json.load(target)
    data['BuildId'] = SourceBuildId
    target.close()
    target = open(PROJECTDIR+i, "wt")
    json.dump(data, target)
    target.close()
    
