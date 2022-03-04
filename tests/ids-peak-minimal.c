#include <stdlib.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef _WIN32
#include <windows.h>
#endif

#include <ids_peak_comfort_c/ids_peak_comfort_c.h>

void errcheck(int ret, int line) {
    if (ret != PEAK_STATUS_SUCCESS) {
	fprintf(stderr,"!! Error code %d, line %d\n",ret,line);
    }
}

int main() {

    int ret = SetDllDirectoryA("C:\\Program Files\\IDS\\ids_peak\\comfort_sdk\\api\\lib\\x86_64");

    printf("Set DLL path returns: %d\n", ret);

    errcheck(peak_Library_Init(), __LINE__);

    uint32_t majorVersionNo = 0;
    uint32_t minorVersionNo = 0;
    uint32_t subMinorVersionNo = 0;
    uint32_t patchVersionNo = 0;

    errcheck( peak_Library_GetVersion(&majorVersionNo, &minorVersionNo, &subMinorVersionNo, &patchVersionNo), __LINE__);

    printf("IDS Peak version: %d.%d.%d-%d", majorVersionNo, minorVersionNo, subMinorVersionNo, patchVersionNo);

    errcheck( peak_Library_Exit(), __LINE__);
}