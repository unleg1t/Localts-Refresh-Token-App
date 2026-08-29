package org.localts.proxy;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

final class ProtocolIO {
   private ProtocolIO() {
   }

   static int readVarInt(InputStream in) throws IOException {
      int value = 0;
      int pos = 0;

      do {
         int b = in.read();
         if (b == -1) {
            throw new EOFException("stream closed mid-VarInt");
         }

         value |= (b & 127) << pos;
         if ((b & 128) == 0) {
            return value;
         }

         pos += 7;
      } while(pos < 32);

      throw new IOException("VarInt too big");
   }

   static void writeVarInt(ByteArrayOutputStream out, int value) {
      while((value & -128) != 0) {
         out.write(value & 127 | 128);
         value >>>= 7;
      }

      out.write(value);
   }

   static int varIntSize(int value) {
      int n;
      for(n = 1; (value & -128) != 0; ++n) {
         value >>>= 7;
      }

      return n;
   }

   static byte[] deflate(byte[] data) {
      Deflater d = new Deflater();
      d.setInput(data);
      d.finish();
      ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
      byte[] buf = new byte[4096];

      while(!d.finished()) {
         int n = d.deflate(buf);
         out.write(buf, 0, n);
      }

      d.end();
      return out.toByteArray();
   }

   static byte[] inflate(byte[] data, int expected) throws IOException {
      Inflater inf = new Inflater();
      inf.setInput(data);
      byte[] out = new byte[expected];

      byte[] var11;
      try {
         int total;
         int n;
         for(total = 0; total < expected && !inf.finished(); total += n) {
            n = inf.inflate(out, total, expected - total);
            if (n == 0 && inf.needsInput()) {
               break;
            }
         }

         if (total != expected) {
            throw new IOException("bad compressed length");
         }

         var11 = out;
      } catch (DataFormatException e) {
         throw new IOException("zlib error", e);
      } finally {
         inf.end();
      }

      return var11;
   }

   static final class Reader {
      private final byte[] buf;
      private int pos;

      Reader(byte[] buf) {
         this.buf = buf;
      }

      int readVarInt() {
         int value = 0;
         int p = 0;

         int b;
         do {
            b = this.buf[this.pos++] & 255;
            value |= (b & 127) << p;
            p += 7;
            if (p > 35) {
               throw new RuntimeException("VarInt too big");
            }
         } while((b & 128) != 0);

         return value;
      }

      String readString() {
         int len = this.readVarInt();
         String s = new String(this.buf, this.pos, len, StandardCharsets.UTF_8);
         this.pos += len;
         return s;
      }

      int readUnsignedShort() {
         return (this.buf[this.pos++] & 255) << 8 | this.buf[this.pos++] & 255;
      }

      byte[] readPrefixedBytes() {
         int len = this.readVarInt();
         byte[] b = new byte[len];
         System.arraycopy(this.buf, this.pos, b, 0, len);
         this.pos += len;
         return b;
      }

      byte[] readRemaining() {
         byte[] b = new byte[this.buf.length - this.pos];
         System.arraycopy(this.buf, this.pos, b, 0, b.length);
         this.pos = this.buf.length;
         return b;
      }
   }

   static final class Writer {
      private final ByteArrayOutputStream out = new ByteArrayOutputStream();

      Writer varInt(int value) {
         ProtocolIO.writeVarInt(this.out, value);
         return this;
      }

      Writer string(String s) {
         byte[] b = s.getBytes(StandardCharsets.UTF_8);
         this.varInt(b.length);
         this.out.writeBytes(b);
         return this;
      }

      Writer unsignedShort(int value) {
         this.out.write(value >> 8 & 255);
         this.out.write(value & 255);
         return this;
      }

      Writer uuid(UUID id) {
         this.longBE(id.getMostSignificantBits());
         this.longBE(id.getLeastSignificantBits());
         return this;
      }

      Writer longBE(long v) {
         for(int i = 7; i >= 0; --i) {
            this.out.write((int)(v >> i * 8) & 255);
         }

         return this;
      }

      Writer prefixedBytes(byte[] b) {
         this.varInt(b.length);
         this.out.writeBytes(b);
         return this;
      }

      Writer raw(byte[] b) {
         this.out.writeBytes(b);
         return this;
      }

      Writer bool(boolean b) {
         this.out.write(b ? 1 : 0);
         return this;
      }

      byte[] toBytes() {
         return this.out.toByteArray();
      }
   }
}
