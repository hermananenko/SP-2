.386
.model flat, stdcall
option casemap :none

include     C:\masm32\include\masm32rt.inc

includelib  C:\masm32\lib\masm32rt.lib

.data
.code
main proc
	mov edx, 0
	mov ebx,4
	mov ecx,10
	sub ebx,ecx
	mov ecx,5
	sub ebx,ecx
	mov eax,101
	idiv ebx
	fn MessageBoxA,0,str$(eax),"1_6-2-Java-IO-83-Ananenko",MB_OK
	ret
main endp
start:
	invoke main
	invoke ExitProcess,0
end start