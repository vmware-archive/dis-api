#! /bin/sh -e

ANDROID_VERSION=18
ANDROID_ABI=armeabi-v7a
EMULATOR_NAME=test
EMULATOR_ARGS="-no-skin -no-window"

while getopts "v:a:n:i" flag
do
	case $flag in
		v) ANDROID_VERSION=$OPTARG ;;
		a) ANDROID_ABI=$OPTARG ;;
		n) EMULATOR_NAME=$OPTARG ;;
		i) EMULATOR_ARGS= ;; # interactive
	esac
done
shift $((OPTIND - 1))

ANDROID_TARGET=android-$ANDROID_VERSION
EMULATOR_PID_FILE=~/.android/avd/"$EMULATOR_NAME.avd"/hardware-qemu.ini.lock

check_running() {
    [ -f "$EMULATOR_PID_FILE" ] && kill -0 "$(cat "$EMULATOR_PID_FILE")"
}

if check_running
then
    kill "$(cat "$EMULATOR_PID_FILE")"
fi

android delete avd -n "$EMULATOR_NAME" || true

echo no | android create avd --force --name "$EMULATOR_NAME" --target $ANDROID_TARGET --abi $ANDROID_ABI
emulator -avd $EMULATOR_NAME $EMULATOR_ARGS -no-audio &
sleep 1
check_running
"$(dirname "$0")"/android-wait-for-emulator
adb shell input keyevent 82
