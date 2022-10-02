/**
 * The ClassFileServer implements a ClassServer that
 * reads class files from the file system. See the
 * doc for the "Main" method for how to run this
 * server.
 */
public class ClassFileServer extends ClassServer {

  private java.io.File[] codebases;


  //
  // -- CONSTRUCTORS -----------------------------------------------
  //

  /**
   * Constructs a ClassFileServer.
   * @param classpath the classpath where the server locates classes
   */
  public ClassFileServer() throws java.io.IOException {
    this(0, null);
  }


  /**
   * Constructs a ClassFileServer.
   * @param classpath the classpath where the server locates classes
   */
  public ClassFileServer(int port) throws java.io.IOException {
    this(port, null);
  }


  /**
   * Constructs a ClassFileServer.
   * @param classpath the classpath where the server locates classes
   */
  public ClassFileServer(String paths) throws java.io.IOException {
    this(0, paths);
  }


  /**
   * Constructs a ClassFileServer.
   * @param port the port to bound the server to
   * @param classpath the classpath where the server locates classes
   */
  public ClassFileServer(int port, String paths) throws java.io.IOException {
    super(port);
    if (paths != null) codebases = findClasspathRoots(paths);
    printMessage();
  }


  //
  // -- PUBLIC METHODS -----------------------------------------------
  //

  public static boolean isPortAlreadyBound(int port) {
    java.net.Socket socket = null;
    try {
      socket = new java.net.Socket(java.net.InetAddress.getLocalHost(), port);
      // if we can connect to the port it means the server already exists
      return true;
    } catch (java.io.IOException e) {
      return false;
    } finally {
      try {
        if (socket != null) socket.close();
      } catch (java.io.IOException e) {
      }
    }
  }


  /**
   * Main method to create the class server that reads
   * class files. This takes two optional command line arguments, the
   * port on which the server accepts requests and the
   * root of the classpath. To start up the server: <br><br>
   *
   * <code>   java ClassFileServer [&lt;classpath>] [&lt;port>]
   * </code><br><br>
   *
   * The codebase of an RMI server using this webserver would
   * simply contain a URL with the host and port of the web
   * server (if the webserver's classpath is the same as
   * the RMI server's classpath): <br><br>
   *
   * <code>   java -Djava.rmi.server.codebase=http://zaphod:2001/ RMIServer
   * </code> <br><br>
   *
   * You can create your own class server inside your RMI server
   * application instead of running one separately. In your server
   * main simply create a ClassFileServer: <br><br>
   *
   * <code>   new ClassFileServer(port, classpath);
   * </code>
   */
  public static void main(String args[]) {
    int port = 0;
    String classpath = null;
    if (args.length >= 1) {
      port = Integer.parseInt(args[0]);
    }
    if (args.length >= 2) {
      classpath = args[1];
    }
    try {
      new ClassFileServer(port, classpath);
    } catch (java.io.IOException e) {
      System.out.println("Unable to start ClassServer: " + e.getMessage());
      e.printStackTrace();
    }
  }



  //
  // -- PROTECTED METHODS -----------------------------------------------
  //

  /**
   * Returns an array of bytes containing the bytecodes for
   * the class represented by the argument <b>path</b>.
   * The <b>path</b> is a dot separated class name with
   * the ".class" extension removed.
   *
   * @return the bytecodes for the class
   * @exception ClassNotFoundException if the class corresponding
   * to <b>path</b> could not be loaded.
   */
  protected byte[] getBytes(String path) throws java.io.IOException, ClassNotFoundException {
    byte[] b = null;
    if (codebases == null) {
      // reading from resources in the classpath
      b = getBytesFromResource(path);
      if (b != null) return b;
    } else {
      for (int i = 0; i < codebases.length; i++) {
        try {
          if (codebases[i].isDirectory()) {
            b = getBytesFromDirectory(path, codebases[i]);
          } else {
            b = getBytesFromArchive(path, codebases[i]);
          }
          if (b != null) return b;
        } catch (java.io.IOException e) {
        }
      }
    }
    throw new ClassNotFoundException("Cannot find class " + path);
  }



  //
  // -- PRIVATE METHODS -----------------------------------------------
  //

  /**
   * Returns an array of bytes containing the bytecodes for
   * the class represented by the argument <b>path</b>.
   * The <b>path</b> is a dot separated class name with
   * the ".class" extension removed.
   * @param path the fqn of the class
   * @param codeBase the File that must be a jar or zip archive that may contain the class
   * @return the bytecodes for the class
   * @exception java.io.IOException if the class cannot be read
   */
  private byte[] getBytesFromResource(String path) throws java.io.IOException {
    String filename = path.replace('.', '/') + ".class";
    java.io.InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
    if (in == null) return null;
    int length = in.available();
    //System.out.println("ClassFileServer reading: " + filename+"  length="+length+" from classpath");
    if (length == -1) {
      throw new java.io.IOException("File length is unknown: " + filename);
    } else {
      return getBytesFromInputStream(in, length);
    }
  }


  /**
   * Returns an array of bytes containing the bytecodes for
   * the class represented by the argument <b>path</b>.
   * The <b>path</b> is a dot separated class name with
   * the ".class" extension removed.
   * @param path the fqn of the class
   * @param codeBase the File that must be a jar or zip archive that may contain the class
   * @return the bytecodes for the class
   * @exception java.io.IOException if the class cannot be read
   */
  private byte[] getBytesFromArchive(String path, java.io.File archive) throws java.io.IOException {
    String filename = path.replace('.', '/') + ".class";
    java.util.zip.ZipFile jarFile = new java.util.zip.ZipFile(archive);
    java.util.zip.ZipEntry zipEntry = jarFile.getEntry(filename);
    if (zipEntry == null) return null;
    int length = (int) (zipEntry.getSize());
    //System.out.println("ClassFileServer reading: " + filename+"  length="+length+" from jar/xip file "+archive.getAbsolutePath());
    if (length == -1) {
      throw new java.io.IOException("File length is unknown: " + filename);
    } else {
      return getBytesFromInputStream(jarFile.getInputStream(zipEntry), length);
    }
  }


  /**
   * Returns an array of bytes containing the bytecodes for
   * the class represented by the argument <b>path</b>.
   * The <b>path</b> is a dot separated class name with
   * the ".class" extension removed.
   * @param path the fqn of the class
   * @param codeBase the File that must be a directory that may contain the class
   * @return the bytecodes for the class
   * @exception java.io.IOException if the class cannot be read
   */
  private byte[] getBytesFromDirectory(String path, java.io.File directory) throws java.io.IOException {
    java.io.File f = new java.io.File(directory, path.replace('.', java.io.File.separatorChar) + ".class");
    if (!f.exists()) return null;
    int length = (int) (f.length());
    //System.out.println("ClassFileServer reading: " + f.getAbsolutePath()+"  length="+length);
    if (length == 0) {
      throw new java.io.IOException("File length is zero: " + path);
    } else {
      return getBytesFromInputStream(new java.io.FileInputStream(f), length);
    }
  }


  /**
   * Returns an array of bytes containing the bytecodes for
   * the class represented by the InputStream
   * @param in the inputstream of the class file
   * @return the bytecodes for the class
   * @exception java.io.IOException if the class cannot be read
   */
  private byte[] getBytesFromInputStream(java.io.InputStream in, int length) throws java.io.IOException {
    java.io.DataInputStream din = new java.io.DataInputStream(in);
    byte[] bytecodes = new byte[length];
    try {
      din.readFully(bytecodes);
    } finally {
      if (din != null) din.close();
    }
    return bytecodes;
  }


  private void printMessage() {
    System.out.println("To use this ClassFileServer set the property -Djava.rmi.server.codebase=http://" + hostname + ":" + port + "/");
    if (codebases == null) {
      System.out.println(" --> This ClassFileServer is reading resources from classpath");
    } else {
      System.out.println(" --> This ClassFileServer is reading resources from the following paths");
      for (int i = 0; i < codebases.length; i++) {
        System.out.println("     (" + i + ") : " + codebases[i].getAbsolutePath());
      }
    }
  }


  private java.io.File[] findClasspathRoots(String classpath) {
    String pathSeparator = System.getProperty("path.separator");
    java.util.StringTokenizer st = new java.util.StringTokenizer(classpath, pathSeparator);
    int n = st.countTokens();
    java.io.File[] roots = new java.io.File[n];
    for (int i = 0; i < n; i++) {
      roots[i] = new java.io.File(st.nextToken());
    }
    return roots;
  }

}

