.386
.model flat, stdcall
option casemap :none

include     C:\masm32\include\masm32rt.inc

includelib  C:\masm32\lib\masm32rt.lib

.data
.code
main1 proc
	mov eax,777
	fn MessageBoxA,0,str$(eax),"1_6-2-Java-IO-83-Ananenko",MB_OK
	ret
main1 endp

start:
	invoke main1
	invoke ExitProcess,0
end start