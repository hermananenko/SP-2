.386
.model flat, stdcall
option casemap :none

include     C:\masm32\include\masm32rt.inc

includelib  C:\masm32\lib\masm32rt.lib

.data
.code
main proc
	mov eax,149
	ret
main endp

start:
	invoke main
	invoke ExitProcess,0
end start
