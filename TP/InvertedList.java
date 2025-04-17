import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.EOFException;
import java.io.RandomAccessFile;

class RegInvList {
    public static final String INDEX_FILE = "./arqs/invlist_index.bin";



    /*         CRUD OPERATIONS         */
    // CREATE
    private static void writeData(Registro reg, int tam_reg, RandomAccessFile file) throws FileNotFoundException, IOException {
        file.writeByte(0);                                    // Lapide (int)                              1 byte
        file.writeInt(tam_reg);                                 // Tamanho do Registro (int)                 4 bytes
        file.writeInt(reg.getId());                             // ID (int)                                  4 bytes
        file.writeShort(reg.getOrgTitle().length());            // Tamanho string original title (short)     2 bytes
        file.writeBytes(reg.getOrgTitle());                     // original title (string)                   variavel
        file.writeShort(reg.getTitle().length());               // Tamanho string title (short)              2 bytes
        file.writeBytes(reg.getTitle());                        // Title (string)                            variavel
        file.write(reg.getOrgLanguage());                       // original language (string fixa)           2 bytes
        file.writeShort(reg.getOvr().length());                 // Tamanho string overview (short)           2 bytes
        file.writeBytes(reg.getOvr());                          // Overview (string)                         variavel
        file.writeLong(reg.getReleaseDate().toEpochDay());      // Release date (long)                       8 bytes
        file.writeFloat(reg.getPopularity());                   // Popularity (float)                        4 bytes   
        file.writeInt(reg.getVoteCount());                      // Vote count (int)                          4 bytes
        file.writeFloat(reg.getVoteAverage());                  // Vote average (float)                      4 bytes
        file.writeInt(reg.getRuntime());                        // Runtime (int)                             4 bytes
        file.writeByte(reg.getAdult());                         // Adult (boolean)                           1 byte
        file.writeByte(reg.getGenreName().size());              // Quantidade de itens da lista (byte)       1 byte
        
        for (String str : reg.getGenreName()) {
            file.writeShort(str.length());                      // Tamanho da string genre name (short)      2 bytes
            file.writeBytes(str);                               // Genre name (String)                       variavel
        }
    }
}