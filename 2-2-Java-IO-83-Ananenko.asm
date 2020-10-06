.386
.model flat, stdcall
option casemap :none

include     C:\masm32\include\masm32rt.inc

includelib  C:\masm32\lib\masm32rt.lib

.data
.code
main proc
	mov edx,0
	mov eax,100
	mov ebx,30
	idiv ebx
	mov edx,0
	mov ebx,3
	idiv ebx
	mov edx,0
	fn MessageBoxA,0,str$(eax),"1_6-2-Java-IO-83-Ananenko",MB_OK
	ret
main endp
start:
	invoke main
	invoke ExitProcess,0
end start