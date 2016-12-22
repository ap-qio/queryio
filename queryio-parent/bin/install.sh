USER_INSTALL_DIR="$(dirname "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )")"

echo "Detecting system type"
case "$(uname -s)" in

   Darwin)
     echo 'Mac OS X'
     sh $USER_INSTALL_DIR/bin/configure-mac.sh
     ;;

   CYGWIN*|MINGW32*|MSYS*)
     echo 'MS Windows is not supported, Aborting!'
     exit 1
     ;;

   Linux)
     echo 'Linux'
     sh $USER_INSTALL_DIR/bin/configure-linux.sh
     ;;

   *)
     echo 'Other OS, assuming linux' 
     sh $USER_INSTALL_DIR/bin/configure-linux.sh
     ;;
esac