# Copyright (C) 2026 Joel Winarske
# Copyright (C) 2026 Ahmed Wafdy
# SPDX-License-Identifier: MIT
#

python setup_dart_native_assets_toolchain() {
    import os
    
    cc = d.getVar('CC')
    cxx = d.getVar('CXX')
    ar = d.getVar('AR')
    ld = d.getVar('LD')
    
    bindir = os.path.join(d.getVar('STAGING_DIR_NATIVE'), 'usr', 'bin')
    bb.utils.mkdirhier(bindir)

    compilers = {
        'clang': cc,
        'gcc': cc,
        'clang++': cxx,
        'g++': cxx,
        'ar': ar,
        'llvm-ar': ar,
        'ld.lld': ld
    }

    for name, tool in compilers.items():
        if not tool:
            continue
            
        wrapper_path = os.path.join(bindir, name)
        bb.debug(1, f"Creating Dart toolchain wrapper for {name} -> {tool}")
        
        with open(wrapper_path, 'w') as f:
            f.write('#!/bin/sh\n')
            f.write(f'exec {tool} "$@"\n')
        os.chmod(wrapper_path, 0o755)
}

do_compile[prefuncs] += "setup_dart_native_assets_toolchain"