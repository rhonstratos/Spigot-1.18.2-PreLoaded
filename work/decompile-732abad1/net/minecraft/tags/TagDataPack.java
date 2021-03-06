package net.minecraft.tags;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.resources.IResource;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.util.ChatDeserializer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class TagDataPack<T> {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final String PATH_SUFFIX = ".json";
    private static final int PATH_SUFFIX_LENGTH = ".json".length();
    private final Function<MinecraftKey, Optional<T>> idToValue;
    private final String directory;

    public TagDataPack(Function<MinecraftKey, Optional<T>> function, String s) {
        this.idToValue = function;
        this.directory = s;
    }

    public Map<MinecraftKey, Tag.a> load(IResourceManager iresourcemanager) {
        Map<MinecraftKey, Tag.a> map = Maps.newHashMap();
        Iterator iterator = iresourcemanager.listResources(this.directory, (s) -> {
            return s.endsWith(".json");
        }).iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey = (MinecraftKey) iterator.next();
            String s = minecraftkey.getPath();
            MinecraftKey minecraftkey1 = new MinecraftKey(minecraftkey.getNamespace(), s.substring(this.directory.length() + 1, s.length() - TagDataPack.PATH_SUFFIX_LENGTH));

            try {
                Iterator iterator1 = iresourcemanager.getResources(minecraftkey).iterator();

                while (iterator1.hasNext()) {
                    IResource iresource = (IResource) iterator1.next();

                    try {
                        InputStream inputstream = iresource.getInputStream();

                        try {
                            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));

                            try {
                                JsonObject jsonobject = (JsonObject) ChatDeserializer.fromJson(TagDataPack.GSON, (Reader) bufferedreader, JsonObject.class);

                                if (jsonobject == null) {
                                    TagDataPack.LOGGER.error("Couldn't load tag list {} from {} in data pack {} as it is empty or null", new Object[]{minecraftkey1, minecraftkey, iresource.getSourceName()});
                                } else {
                                    ((Tag.a) map.computeIfAbsent(minecraftkey1, (minecraftkey2) -> {
                                        return Tag.a.tag();
                                    })).addFromJson(jsonobject, iresource.getSourceName());
                                }
                            } catch (Throwable throwable) {
                                try {
                                    bufferedreader.close();
                                } catch (Throwable throwable1) {
                                    throwable.addSuppressed(throwable1);
                                }

                                throw throwable;
                            }

                            bufferedreader.close();
                        } catch (Throwable throwable2) {
                            if (inputstream != null) {
                                try {
                                    inputstream.close();
                                } catch (Throwable throwable3) {
                                    throwable2.addSuppressed(throwable3);
                                }
                            }

                            throw throwable2;
                        }

                        if (inputstream != null) {
                            inputstream.close();
                        }
                    } catch (RuntimeException | IOException ioexception) {
                        TagDataPack.LOGGER.error("Couldn't read tag list {} from {} in data pack {}", new Object[]{minecraftkey1, minecraftkey, iresource.getSourceName(), ioexception});
                    } finally {
                        IOUtils.closeQuietly(iresource);
                    }
                }
            } catch (IOException ioexception1) {
                TagDataPack.LOGGER.error("Couldn't read tag list {} from {}", new Object[]{minecraftkey1, minecraftkey, ioexception1});
            }
        }

        return map;
    }

    private static void visitDependenciesAndElement(Map<MinecraftKey, Tag.a> map, Multimap<MinecraftKey, MinecraftKey> multimap, Set<MinecraftKey> set, MinecraftKey minecraftkey, BiConsumer<MinecraftKey, Tag.a> biconsumer) {
        if (set.add(minecraftkey)) {
            multimap.get(minecraftkey).forEach((minecraftkey1) -> {
                visitDependenciesAndElement(map, multimap, set, minecraftkey1, biconsumer);
            });
            Tag.a tag_a = (Tag.a) map.get(minecraftkey);

            if (tag_a != null) {
                biconsumer.accept(minecraftkey, tag_a);
            }

        }
    }

    private static boolean isCyclic(Multimap<MinecraftKey, MinecraftKey> multimap, MinecraftKey minecraftkey, MinecraftKey minecraftkey1) {
        Collection<MinecraftKey> collection = multimap.get(minecraftkey1);

        return collection.contains(minecraftkey) ? true : collection.stream().anyMatch((minecraftkey2) -> {
            return isCyclic(multimap, minecraftkey, minecraftkey2);
        });
    }

    private static void addDependencyIfNotCyclic(Multimap<MinecraftKey, MinecraftKey> multimap, MinecraftKey minecraftkey, MinecraftKey minecraftkey1) {
        if (!isCyclic(multimap, minecraftkey, minecraftkey1)) {
            multimap.put(minecraftkey, minecraftkey1);
        }

    }

    public Map<MinecraftKey, Tag<T>> build(Map<MinecraftKey, Tag.a> map) {
        Map<MinecraftKey, Tag<T>> map1 = Maps.newHashMap();

        Objects.requireNonNull(map1);
        Function<MinecraftKey, Tag<T>> function = map1::get;
        Function<MinecraftKey, T> function1 = (minecraftkey) -> {
            return ((Optional) this.idToValue.apply(minecraftkey)).orElse((Object) null);
        };
        Multimap<MinecraftKey, MinecraftKey> multimap = HashMultimap.create();

        map.forEach((minecraftkey, tag_a) -> {
            tag_a.visitRequiredDependencies((minecraftkey1) -> {
                addDependencyIfNotCyclic(multimap, minecraftkey, minecraftkey1);
            });
        });
        map.forEach((minecraftkey, tag_a) -> {
            tag_a.visitOptionalDependencies((minecraftkey1) -> {
                addDependencyIfNotCyclic(multimap, minecraftkey, minecraftkey1);
            });
        });
        Set<MinecraftKey> set = Sets.newHashSet();

        map.keySet().forEach((minecraftkey) -> {
            visitDependenciesAndElement(map, multimap, set, minecraftkey, (minecraftkey1, tag_a) -> {
                tag_a.build(function, function1).ifLeft((collection) -> {
                    TagDataPack.LOGGER.error("Couldn't load tag {} as it is missing following references: {}", minecraftkey1, collection.stream().map(Objects::toString).collect(Collectors.joining(", ")));
                }).ifRight((tag) -> {
                    map1.put(minecraftkey1, tag);
                });
            });
        });
        return map1;
    }

    public Map<MinecraftKey, Tag<T>> loadAndBuild(IResourceManager iresourcemanager) {
        return this.build(this.load(iresourcemanager));
    }
}
