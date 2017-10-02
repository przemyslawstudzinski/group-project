Set shell = CreateObject("WScript.Shell")
shell.CurrentDirectory = "./Server"
shell.Run "server.bat", 0, True