#!/bin/bash

# Android APK Build Script
# This script builds an Android APK using Gradle Wrapper

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
BUILD_TYPE="debug"  # Default build type (debug or release)
CLEAN_BUILD=false
INSTALL_APK=false
APK_OUTPUT_DIR="app/build/outputs/apk"

# Function to print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help          Show this help message"
    echo "  -r, --release       Build release APK (default: debug)"
    echo "  -d, --debug         Build debug APK"
    echo "  -c, --clean         Clean build (gradlew clean)"
    echo "  -i, --install       Install APK after building"
    echo "  --lint              Run lint checks before building"
    echo "  --test              Run tests before building"
    echo ""
    echo "Examples:"
    echo "  $0                  # Build debug APK"
    echo "  $0 --release        # Build release APK"
    echo "  $0 --clean --debug  # Clean and build debug APK"
    echo "  $0 --release --install  # Build release APK and install"
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_usage
            exit 0
            ;;
        -r|--release)
            BUILD_TYPE="release"
            shift
            ;;
        -d|--debug)
            BUILD_TYPE="debug"
            shift
            ;;
        -c|--clean)
            CLEAN_BUILD=true
            shift
            ;;
        -i|--install)
            INSTALL_APK=true
            shift
            ;;
        --lint)
            RUN_LINT=true
            shift
            ;;
        --test)
            RUN_TESTS=true
            shift
            ;;
        *)
            print_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Check if gradlew exists
if [ ! -f "./gradlew" ]; then
    print_error "gradlew not found in current directory!"
    print_info "Make sure you're in the Android project root directory"
    exit 1
fi

# Make gradlew executable
chmod +x ./gradlew

# Print build configuration
print_info "=== Android APK Build Configuration ==="
print_info "Build Type: $BUILD_TYPE"
print_info "Clean Build: $CLEAN_BUILD"
print_info "Install APK: $INSTALL_APK"
print_info "Output Directory: $APK_OUTPUT_DIR"
echo ""

# Clean build if requested
if [ "$CLEAN_BUILD" = true ]; then
    print_info "Cleaning project..."
    ./gradlew clean
    print_success "Project cleaned successfully"
fi

# Run lint if requested
if [ "$RUN_LINT" = true ]; then
    print_info "Running lint checks..."
    ./gradlew lint
    print_success "Lint checks completed"
fi

# Run tests if requested
if [ "$RUN_TESTS" = true ]; then
    print_info "Running unit tests..."
    ./gradlew test
    print_success "Unit tests completed"
fi

# Build the APK
print_info "Building $BUILD_TYPE APK..."
if [ "$BUILD_TYPE" = "release" ]; then
    ./gradlew assembleRelease
    APK_PATH="$APK_OUTPUT_DIR/release/app-release.apk"
    UNSIGNED_APK_PATH="$APK_OUTPUT_DIR/release/app-release-unsigned.apk"
else
    ./gradlew assembleDebug
    APK_PATH="$APK_OUTPUT_DIR/debug/app-debug.apk"
fi

# Check if APK was built successfully
if [ -f "$APK_PATH" ]; then
    print_success "APK built successfully!"
    print_info "APK Location: $APK_PATH"
    
    # Get APK file size
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    print_info "APK Size: $APK_SIZE"
    
    # Show APK info
    print_info "=== APK Information ==="
    ls -la "$APK_PATH"
    
elif [ -f "$UNSIGNED_APK_PATH" ]; then
    print_warning "Unsigned APK created: $UNSIGNED_APK_PATH"
    print_warning "You need to sign the APK for release"
    APK_PATH="$UNSIGNED_APK_PATH"
else
    print_error "APK build failed! No APK file found."
    exit 1
fi

# Install APK if requested
if [ "$INSTALL_APK" = true ]; then
    print_info "Installing APK..."
    
    # Check if adb is available
    if command -v adb &> /dev/null; then
        # Check if device is connected
        DEVICES=$(adb devices | grep -v "List of devices attached" | grep "device$" | wc -l)
        
        if [ "$DEVICES" -gt 0 ]; then
            print_info "Installing APK on connected device..."
            adb install -r "$APK_PATH"
            print_success "APK installed successfully!"
        else
            print_warning "No Android device connected via ADB"
            print_info "Connect a device or start an emulator, then run:"
            print_info "adb install -r $APK_PATH"
        fi
    else
        print_warning "ADB not found in PATH"
        print_info "Install Android SDK Platform Tools to use ADB"
        print_info "Manual installation: adb install -r $APK_PATH"
    fi
fi

# Final summary
print_success "=== Build Summary ==="
print_success "Build Type: $BUILD_TYPE"
print_success "APK Location: $APK_PATH"
print_success "Build completed successfully!"

# Additional notes for release builds
if [ "$BUILD_TYPE" = "release" ]; then
    echo ""
    print_warning "=== Release Build Notes ==="
    print_warning "• Make sure your release APK is properly signed"
    print_warning "• Test the release APK thoroughly before distribution"
    print_warning "• Consider running: ./gradlew bundleRelease for Play Store"
fi