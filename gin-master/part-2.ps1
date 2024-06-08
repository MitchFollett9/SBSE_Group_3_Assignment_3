# $javaPath = "C:\Program Files\Java\jdk-17\bin\java"
# $jarFile = "build/gin.jar"
# $javaArgs = "examples/locoGP/SortInsertion.java"

# Start-Process -FilePath $javaPath -ArgumentList "-jar", $jarFile, $javaArgs -Wait -RedirectStandardOutput output.txt

# Get-Content output.txt | Select-String -Pattern ".*(best|Initial)"

# # Remove-Item output.txt

# $javaPath = "C:\Program Files\Java\jdk-17\bin\java"
# $jarFile = "build/gin.jar"
# $javaArgs = "examples/locoGP/SortInsertion.java"

# $outputFilePath = "output.txt"
# Start-Process -FilePath $javaPath -ArgumentList "-jar", $jarFile, $javaArgs -Wait -RedirectStandardOutput $outputFilePath

# # Define regular expressions for lines you want to filter
# $patterns = "Best patch found", "Best execution time", "Number of removals"

# # Read output file and filter lines
# $filteredLines = Get-Content $outputFilePath | Where-Object { $_ -match "^($($patterns -join '|'))" }

# # Output filtered lines to results.txt
# $filteredLines | Out-File -FilePath "results.txt" -Append
# Define the number of times you want to run the script
$numberOfRuns = 10

# Loop to run the script multiple times
for ($i = 1; $i -le $numberOfRuns; $i++) {
    Write-Host "Running script $i"
    # Your script here
    $javaPath = "C:\Program Files\Java\jdk-17\bin\java"
    $jarFile = "build/gin.jar"
    $javaArgs = "examples/locoGP/SortInsertion.java"

    $outputFilePath = "output$i.txt"
    Start-Process -FilePath $javaPath -ArgumentList "-jar", $jarFile, $javaArgs -Wait -RedirectStandardOutput $outputFilePath

    # Define regular expressions for lines you want to filter
    $patterns = "Best patch found", "Best execution time", "Found at step", "after final search"

    # Read output file and filter lines
    $filteredLines = Get-Content $outputFilePath | Where-Object { $_ -match "^($($patterns -join '|'))" }

    # Output filtered lines to results.txt
    $filteredLines | Out-File -FilePath "results.txt" -Append

    Write-Host "Script $i completed"
}
