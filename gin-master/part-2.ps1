$javaPath = "C:\Program Files\Java\jdk-17\bin\java"
$jarFile = "build/gin.jar"
$javaArgs = "examples/locoGP/SortInsertion.java"

Start-Process -FilePath $javaPath -ArgumentList "-jar", $jarFile, $javaArgs -Wait -RedirectStandardOutput output.txt

Get-Content output.txt | Select-String -Pattern ".*(best|Initial)"

# Remove-Item output.txt