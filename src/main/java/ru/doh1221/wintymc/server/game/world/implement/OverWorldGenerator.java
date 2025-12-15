package ru.doh1221.wintymc.server.game.world.implement;

import ru.doh1221.wintymc.server.game.objects.blocks.BlockRegistry;
import ru.doh1221.wintymc.server.game.world.World;
import ru.doh1221.wintymc.server.game.world.chunk.Chunk;
import ru.doh1221.wintymc.server.game.world.chunk.IChunkProvider;

import java.util.Arrays;
import java.util.Random;

public class OverWorldGenerator implements IChunkProvider {

    private Random j;
    private NoiseGeneratorOctaves k;
    private NoiseGeneratorOctaves l;
    private NoiseGeneratorOctaves m;
    private NoiseGeneratorOctaves n;
    private NoiseGeneratorOctaves o;
    public NoiseGeneratorOctaves a;
    public NoiseGeneratorOctaves b;
    public NoiseGeneratorOctaves c;
    private World p;
    private double[] q;
    private double[] r = new double[256];
    private double[] s = new double[256];
    private double[] t = new double[256];
    private MapGenBase u = new MapGenCaves();
    private BiomeBase[] v;
    double[] d;
    double[] e;
    double[] f;
    double[] g;
    double[] h;
    int[][] i = new int[32][32];
    private double[] w;

    public OverWorldGenerator(World world, long i) {
        this.p = world;
        this.j = new Random(i);
        this.k = new NoiseGeneratorOctaves(this.j, 16);
        this.l = new NoiseGeneratorOctaves(this.j, 16);
        this.m = new NoiseGeneratorOctaves(this.j, 8);
        this.n = new NoiseGeneratorOctaves(this.j, 4);
        this.o = new NoiseGeneratorOctaves(this.j, 4);
        this.a = new NoiseGeneratorOctaves(this.j, 10);
        this.b = new NoiseGeneratorOctaves(this.j, 16);
        this.c = new NoiseGeneratorOctaves(this.j, 8);
    }

    public void a(int i, int j, byte[] abyte, BiomeBase[] abiomebase, double[] adouble) {
        byte b0 = 4;
        byte b1 = 64;
        int k = b0 + 1;
        byte b2 = 17;
        int l = b0 + 1;

        this.q = this.a(this.q, i * b0, 0, j * b0, k, b2, l);

        for (int i1 = 0; i1 < b0; ++i1) {
            for (int j1 = 0; j1 < b0; ++j1) {
                for (int k1 = 0; k1 < 16; ++k1) {
                    double d0 = 0.125D;
                    double d1 = this.q[((i1 + 0) * l + j1 + 0) * b2 + k1 + 0];
                    double d2 = this.q[((i1 + 0) * l + j1 + 1) * b2 + k1 + 0];
                    double d3 = this.q[((i1 + 1) * l + j1 + 0) * b2 + k1 + 0];
                    double d4 = this.q[((i1 + 1) * l + j1 + 1) * b2 + k1 + 0];
                    double d5 = (this.q[((i1 + 0) * l + j1 + 0) * b2 + k1 + 1] - d1) * d0;
                    double d6 = (this.q[((i1 + 0) * l + j1 + 1) * b2 + k1 + 1] - d2) * d0;
                    double d7 = (this.q[((i1 + 1) * l + j1 + 0) * b2 + k1 + 1] - d3) * d0;
                    double d8 = (this.q[((i1 + 1) * l + j1 + 1) * b2 + k1 + 1] - d4) * d0;

                    for (int l1 = 0; l1 < 8; ++l1) {
                        double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;

                        for (int i2 = 0; i2 < 4; ++i2) {
                            int j2 = i2 + i1 * 4 << 11 | 0 + j1 * 4 << 7 | k1 * 8 + l1;
                            short short1 = 128;
                            double d14 = 0.25D;
                            double d15 = d10;
                            double d16 = (d11 - d10) * d14;

                            for (int k2 = 0; k2 < 4; ++k2) {
                                double d17 = adouble[(i1 * 4 + i2) * 16 + j1 * 4 + k2];
                                int l2 = 0;

                                if (k1 * 8 + l1 < b1) {
                                    if (d17 < 0.5D && k1 * 8 + l1 >= b1 - 1) {
                                        l2 = BlockRegistry.getByBlockName("ice").getID();
                                    } else {
                                        l2 = BlockRegistry.getByBlockName("stationary_water").getID();
                                    }
                                }

                                if (d15 > 0.0D) {
                                    l2 = BlockRegistry.getByBlockName("stone").getID();
                                }

                                abyte[j2] = (byte) l2;
                                j2 += short1;
                                d15 += d16;
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    public void a(int i, int j, byte[] abyte, BiomeBase[] abiomebase) {
        byte b0 = 64;
        double d0 = 0.03125D;

        this.r = this.n.a(this.r, (double) (i * 16), (double) (j * 16), 0.0D, 16, 16, 1, d0, d0, 1.0D);
        this.s = this.n.a(this.s, (double) (i * 16), 109.0134D, (double) (j * 16), 16, 1, 16, d0, 1.0D, d0);
        this.t = this.o.a(this.t, (double) (i * 16), (double) (j * 16), 0.0D, 16, 16, 1, d0 * 2.0D, d0 * 2.0D, d0 * 2.0D);

        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                BiomeBase biomebase = abiomebase[k + l * 16];
                boolean flag = this.r[k + l * 16] + this.j.nextDouble() * 0.2D > 0.0D;
                boolean flag1 = this.s[k + l * 16] + this.j.nextDouble() * 0.2D > 3.0D;
                int i1 = (int) (this.t[k + l * 16] / 3.0D + 3.0D + this.j.nextDouble() * 0.25D);
                int j1 = -1;
                byte b1 = biomebase.p;
                byte b2 = biomebase.q;

                for (int k1 = 127; k1 >= 0; --k1) {
                    int l1 = (l * 16 + k) * 128 + k1;

                    if (k1 <= 0 + this.j.nextInt(5)) {
                        abyte[l1] = (byte) BlockRegistry.getByBlockName("bedrock").getID();
                    } else {
                        byte b3 = abyte[l1];

                        if (b3 == 0) {
                            j1 = -1;
                        } else if (b3 == BlockRegistry.getByBlockName("stone").getID()) {
                            if (j1 == -1) {
                                if (i1 <= 0) {
                                    b1 = 0;
                                    b2 = (byte) BlockRegistry.getByBlockName("stone").getID();
                                } else if (k1 >= b0 - 4 && k1 <= b0 + 1) {
                                    b1 = biomebase.p;
                                    b2 = biomebase.q;
                                    if (flag1) {
                                        b1 = 0;
                                    }

                                    if (flag1) {
                                        b2 = (byte) BlockRegistry.getByBlockName("gravel").getID();
                                    }

                                    if (flag) {
                                        b1 = (byte) BlockRegistry.getByBlockName("sand").getID();
                                    }

                                    if (flag) {
                                        b2 = (byte) BlockRegistry.getByBlockName("sand").getID();
                                    }
                                }

                                if (k1 < b0 && b1 == 0) {
                                    b1 = (byte) BlockRegistry.getByBlockName("stationary_water").getID();
                                }

                                j1 = i1;
                                if (k1 >= b0 - 1) {
                                    abyte[l1] = b1;
                                } else {
                                    abyte[l1] = b2;
                                }
                            } else if (j1 > 0) {
                                --j1;
                                abyte[l1] = b2;
                                if (j1 == 0 && b2 == BlockRegistry.getByBlockName("sand").getID()) {
                                    j1 = this.j.nextInt(4);
                                    b2 = (byte) BlockRegistry.getByBlockName("sandstone").getID();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Chunk getChunkAt(int i, int j) {
        return this.getOrCreate(i, j);
    }

    @Override
    public Chunk getOrCreate(int i, int j) {
        this.j.setSeed((long) i * 341873128712L + (long) j * 132897987541L);
        byte[] abyte = new byte['\u8000'];
        Chunk chunk = new Chunk(i, j, abyte);
        this.v = this.p.getWorldChunkManager().a(this.v, i * 16, j * 16, 16, 16);
        double[] adouble = this.p.getWorldChunkManager().temperature;

        this.a(i, j, abyte, this.v, adouble);
        this.a(i, j, abyte, this.v);
        this.u.a(this, this.p, i, j, abyte);
        return chunk;
    }

    private double[] a(double[] adouble, int i, int j, int k, int l, int i1, int j1) {
        if (adouble == null) {
            adouble = new double[l * i1 * j1];
        }

        double d0 = 684.412D;
        double d1 = 684.412D;
        double[] adouble1 = this.p.getWorldChunkManager().temperature;
        double[] adouble2 = this.p.getWorldChunkManager().rain;

        this.g = this.a.a(this.g, i, k, l, j1, 1.121D, 1.121D, 0.5D);
        this.h = this.b.a(this.h, i, k, l, j1, 200.0D, 200.0D, 0.5D);
        this.d = this.m.a(this.d, (double) i, (double) j, (double) k, l, i1, j1, d0 / 80.0D, d1 / 160.0D, d0 / 80.0D);
        this.e = this.k.a(this.e, (double) i, (double) j, (double) k, l, i1, j1, d0, d1, d0);
        this.f = this.l.a(this.f, (double) i, (double) j, (double) k, l, i1, j1, d0, d1, d0);
        int k1 = 0;
        int l1 = 0;
        int i2 = 16 / l;

        for (int j2 = 0; j2 < l; ++j2) {
            int k2 = j2 * i2 + i2 / 2;

            for (int l2 = 0; l2 < j1; ++l2) {
                int i3 = l2 * i2 + i2 / 2;
                double d2 = adouble1[k2 * 16 + i3];
                double d3 = adouble2[k2 * 16 + i3] * d2;
                double d4 = 1.0D - d3;

                d4 *= d4;
                d4 *= d4;
                d4 = 1.0D - d4;
                double d5 = (this.g[l1] + 256.0D) / 512.0D;

                d5 *= d4;
                if (d5 > 1.0D) {
                    d5 = 1.0D;
                }

                double d6 = this.h[l1] / 8000.0D;

                if (d6 < 0.0D) {
                    d6 = -d6 * 0.3D;
                }

                d6 = d6 * 3.0D - 2.0D;
                if (d6 < 0.0D) {
                    d6 /= 2.0D;
                    if (d6 < -1.0D) {
                        d6 = -1.0D;
                    }

                    d6 /= 1.4D;
                    d6 /= 2.0D;
                    d5 = 0.0D;
                } else {
                    if (d6 > 1.0D) {
                        d6 = 1.0D;
                    }

                    d6 /= 8.0D;
                }

                if (d5 < 0.0D) {
                    d5 = 0.0D;
                }

                d5 += 0.5D;
                d6 = d6 * (double) i1 / 16.0D;
                double d7 = (double) i1 / 2.0D + d6 * 4.0D;

                ++l1;

                for (int j3 = 0; j3 < i1; ++j3) {
                    double d8 = 0.0D;
                    double d9 = ((double) j3 - d7) * 12.0D / d5;

                    if (d9 < 0.0D) {
                        d9 *= 4.0D;
                    }

                    double d10 = this.e[k1] / 512.0D;
                    double d11 = this.f[k1] / 512.0D;
                    double d12 = (this.d[k1] / 10.0D + 1.0D) / 2.0D;

                    if (d12 < 0.0D) {
                        d8 = d10;
                    } else if (d12 > 1.0D) {
                        d8 = d11;
                    } else {
                        d8 = d10 + (d11 - d10) * d12;
                    }

                    d8 -= d9;
                    if (j3 > i1 - 4) {
                        double d13 = (double) ((float) (j3 - (i1 - 4)) / 3.0F);

                        d8 = d8 * (1.0D - d13) + -10.0D * d13;
                    }

                    adouble[k1] = d8;
                    ++k1;
                }
            }
        }

        return adouble;
    }

    public boolean isChunkLoaded(int i, int j) {
        return true;
    }

    public void getChunkAt(IChunkProvider ichunkprovider, int i, int j) {
        int k = i * 16;
        int l = j * 16;
        BiomeBase biomebase = this.p.getWorldChunkManager().getBiome(k + 16, l + 16);

        this.j.setSeed(this.p.getSeed());
        long i1 = this.j.nextLong() / 2L * 2L + 1L;
        long j1 = this.j.nextLong() / 2L * 2L + 1L;

        this.j.setSeed((long) i * i1 + (long) j * j1 ^ this.p.getSeed());
        double d0 = 0.25D;
        int k1;
        int l1;
        int i2;

        int j2;

                d0 = 0.5D;
        k1 = (int) ((this.c.a((double) k * d0, (double) l * d0) / 8.0D + this.j.nextDouble() * 4.0D + 4.0D) / 3.0D);
        l1 = 0;
        if (this.j.nextInt(10) == 0) {
            ++l1;
        }

        if (biomebase == BiomeBase.FOREST) {
            l1 += k1 + 5;
        }

        if (biomebase == BiomeBase.RAINFOREST) {
            l1 += k1 + 5;
        }

        if (biomebase == BiomeBase.SEASONAL_FOREST) {
            l1 += k1 + 2;
        }

        if (biomebase == BiomeBase.TAIGA) {
            l1 += k1 + 5;
        }

        if (biomebase == BiomeBase.DESERT) {
            l1 -= 20;
        }

        if (biomebase == BiomeBase.TUNDRA) {
            l1 -= 20;
        }

        if (biomebase == BiomeBase.PLAINS) {
            l1 -= 20;
        }

        int k2;

        byte b0 = 0;

        if (biomebase == BiomeBase.FOREST) {
            b0 = 2;
        }

        if (biomebase == BiomeBase.SEASONAL_FOREST) {
            b0 = 4;
        }

        if (biomebase == BiomeBase.TAIGA) {
            b0 = 2;
        }

        if (biomebase == BiomeBase.PLAINS) {
            b0 = 3;
        }

        int l2;
        int i3;

        byte b1 = 0;

        if (biomebase == BiomeBase.FOREST) {
            b1 = 2;
        }

        if (biomebase == BiomeBase.RAINFOREST) {
            b1 = 10;
        }

        if (biomebase == BiomeBase.SEASONAL_FOREST) {
            b1 = 2;
        }

        if (biomebase == BiomeBase.TAIGA) {
            b1 = 1;
        }

        if (biomebase == BiomeBase.PLAINS) {
            b1 = 10;
        }

        int j3;
        int k3;

        b1 = 0;
        if (biomebase == BiomeBase.DESERT) {
            b1 = 2;
        }

        k2 = 0;
        if (biomebase == BiomeBase.DESERT) {
            k2 += 10;
        }

    }

    public boolean unloadChunks() {
        return false;
    }

    public boolean canSave() {
        return true;
    }
}
