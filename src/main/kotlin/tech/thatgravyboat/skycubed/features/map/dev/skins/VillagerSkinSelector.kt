package tech.thatgravyboat.skycubed.features.map.dev.skins

//? if > 1.21.10 {
import net.minecraft.world.entity.npc.villager.Villager
import net.minecraft.world.entity.npc.villager.VillagerProfession
import net.minecraft.world.entity.npc.villager.VillagerType
//?} else {
/* import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.world.entity.npc.VillagerType
*///?}

data class VillagerSkin(
    val default: String,
    val desert: String = default,
    val savanna: String = default,
    val snow: String = default,
    val swamp: String = default,
    val taiga: String = default,
) : SkinSelector<Villager> {
    override fun getSkin(entity: Villager): String = when (entity.villagerData.type()) {
        in VillagerType.DESERT -> desert
        in VillagerType.SAVANNA -> savanna
        in VillagerType.SNOW -> snow
        in VillagerType.SWAMP -> swamp
        in VillagerType.TAIGA -> taiga
        else -> default
    }

    companion object : SkinSelector<Villager> {
        val NONE = VillagerSkin(
            "http://textures.minecraft.net/texture/d14bff1a38c9154e5ec84ce5cf00c58768e068eb42b2d89a6bbd29787590106b",
            "http://textures.minecraft.net/texture/10bf6df37dac6ca6089d2ba04135f223d4d850df9f09c7ec4eaf8c50764cbc50",
            "http://textures.minecraft.net/texture/dd9ee5d8b58fddd27bc679c0548f55baa845f9f1df5e88c7c5bda6eb9df2b399",
            "http://textures.minecraft.net/texture/d1f11f39fe45271e3a5c603882bb25f3d601ad4f6899c0920fe791283b0bf1e4",
            "http://textures.minecraft.net/texture/9ad7a9e8fe2bdfea03bb1f9fabe45fb10cf69a72e3760e5fd9a70f3384c536ad",
            "http://textures.minecraft.net/texture/61e897719b54b844fa059f04817e13db8abd97e6bdb0624093032b4512f7a1c6",
        )
        val ARMORER = VillagerSkin(
            "http://textures.minecraft.net/texture/f522db92f188ebc7713cf35b4cbaed1cfe2642a5986c3bde993f5cfb3727664c",
            "http://textures.minecraft.net/texture/73a8fdeec03869fe400207803c975f67c95890e3567cfd7e7cda8ea5b2b68773",
            "http://textures.minecraft.net/texture/8b6cd9a9fb4b13f49d25b82a14040c045ff57aa8c279cce20a2503b8184d9b9c",
            "http://textures.minecraft.net/texture/edcb21090cd7e97066a1f1dcae778cda032df8af953767d2a04f4a78dcfc2496",
            "http://textures.minecraft.net/texture/ea7abb127b69338a81b87a17b664214ee9fe0437c8b577a0e3674b1c6d0fca77",
            "http://textures.minecraft.net/texture/6fc9fbe1422d5b0edbe0650e511b1d9c2291460752b066c4f701c0297a6201e8",
        )
        val BUTCHER = VillagerSkin(
            "http://textures.minecraft.net/texture/c6774d2df515eceae9eed291c1b40f94adf71df0ab81c7191402e1a45b3a2087",
            "http://textures.minecraft.net/texture/4a6b7136db906a2af54fd446094f5d4d074c4625665bce5706ad6208b8b383f9",
            "http://textures.minecraft.net/texture/366715115de766c949e8700b7650c8994fd3eeb97bd1836c2e16b5d8d5551b5d",
            "http://textures.minecraft.net/texture/7bb5bfa9ac5412e7d116f9d424e0fd3fa40eedc2c3e7c906c055bb5f4c5c587a",
            "http://textures.minecraft.net/texture/5393370444ee915595630cd900d065c03cc5c61f7a91edd8fec24f204b3eeb4f",
            "http://textures.minecraft.net/texture/89b921ca9e5e7a43ebb904ca5b833d5c9b37f0fe039e9112303c0f9b188ab739",
        )
        val CARTOGRAPHER = VillagerSkin(
            "http://textures.minecraft.net/texture/94248dd0680305ad73b214e8c6b00094e27a4ddd8034676921f905130b858bdb",
            "http://textures.minecraft.net/texture/a6f25ea1c39a3d73579e5330e04c2b173ba2fd5c0ca3452be5ff1aae083f6328",
            "http://textures.minecraft.net/texture/f2f944222ff693fe088bd543c8efdd3246eab6a34978acde774f7e295dee9e16",
            "http://textures.minecraft.net/texture/4143895b04cce7b3ad956620bb48d1aaaa6bbdcc10bb3a8c4aa0a55939f050be",
            "http://textures.minecraft.net/texture/2e040983328da42b4eda931bdcefb39a8816b32045da1ed9f335e538c18f3c41",
            "http://textures.minecraft.net/texture/3e1fb52b7ae82bb091c54c33b7fa6b80c8a0250372b9d1cbd6f796522f6479d",
        )
        val CLERIC = VillagerSkin(
            "http://textures.minecraft.net/texture/a8856eaafad96d76fa3b5edd0e3b5f45ee49a3067306ad94df9ab3bd5b2d142d",
            "http://textures.minecraft.net/texture/d24ba760a61dd256c52b325129f46016ae892232a0dea1715f997f7c4d622bef",
            "http://textures.minecraft.net/texture/46cc8fa8379665fbb8c924e45235da1c988c1c523af1b2479796d4a49af1c5c8",
            "http://textures.minecraft.net/texture/7e3d3635ce411abf1e4f373d161d07b8c47e359b6c56f74b413cb494ac746e2d",
            "http://textures.minecraft.net/texture/1a8e3e224a768bb5771d6e4653e48a54fe6cd095fc399d3ec39b95c2544af054",
            "http://textures.minecraft.net/texture/4bfad3b0fc8d19a7dda68087cf5a5e6865cc9faf2e79edf10af4bfa70a4d4bd9",
        )
        val FARMER = VillagerSkin(
            "http://textures.minecraft.net/texture/55a0b07e36eafdecf059c8cb134a7bf0a167f900966f1099252d903276461cce",
            "http://textures.minecraft.net/texture/355d61a2409eb0b49b3e88b2888467f20a3b06212a10e7b6efb9ce3bc1a0e20f",
            "http://textures.minecraft.net/texture/c9c94faa7ac9b0752dc7da7386b4d8fc34e2916da5b01789275bbcb7dfce7fcb",
            "http://textures.minecraft.net/texture/fd95ad3f37bb323785f8d6816763e5a739e2814d611a7ab4afff976f91729faf",
            "http://textures.minecraft.net/texture/e2cfc7eade016a969c2b3a87e010a02ac910df60d3714f76184b2c17a703101e",
            "http://textures.minecraft.net/texture/608bdb53c55fef32a0658e1c7966614af0bff6091249b8fe3b77a0275da82e43",
        )
        val FISHERMAN = VillagerSkin(
            "http://textures.minecraft.net/texture/ac15e5fb56fa16b0747b1bcb05335f55d1fa31561c082b5e3643db5565410852",
            "http://textures.minecraft.net/texture/74111111e532d68f0e4f913a4e3aedd0c9dfb2847a8aaf1ffa52b3dabcff86ed",
            "http://textures.minecraft.net/texture/6acacef5f04bed42e8a808ccec39889e666fb1299e99a9e09060f8cf29e6baf6",
            "http://textures.minecraft.net/texture/61d644761f706d31c99a593c8d5f7cbbd4372d73fbee8464f482fa6c139d97d4",
            "http://textures.minecraft.net/texture/d223664886d96643d3afdc4a2ac72ebc2bcd229e517519e15c3e7c1570ad745e",
            "http://textures.minecraft.net/texture/ac7f44b511f7063187d0fe12c74a0cb8c93f34d0f587338b2a9c22f3fa2f212",
        )
        val FLETCHER = VillagerSkin(
            "http://textures.minecraft.net/texture/17532e90c573a394c7802aa4158305802b59e67f2a2b7e3fd0363aa6ea42b841",
            "http://textures.minecraft.net/texture/fd4d13baf65ee197dfc6ec28657d25d89f472662204e13cbf761f9412c891335",
            "http://textures.minecraft.net/texture/fac222efc6ab4bd1c70351de007e37bf3d028c1d93f08efeaea8a07ecb62867b",
            "http://textures.minecraft.net/texture/1e126158220e946517b2c20e13805928df06e19c1101bdea2bc5a4fc95f9c011",
            "http://textures.minecraft.net/texture/12ed931ee6b77539d2fd7d271617db6b19365cb0e3c52eac005272301d07ac74",
            "http://textures.minecraft.net/texture/99c45ed238e0b0c664dde09de64ba9e6038dd231dd58466fbdcdcc911e0ffbd2",
        )
        val LEATHERWORKER = VillagerSkin(
            "http://textures.minecraft.net/texture/f76cf8b7378e889395d538e6354a17a3de6b294bb6bf8db9c701951c68d3c0e6",
            "http://textures.minecraft.net/texture/ee61ecbb86487ed16fedb275db92c9c5043a830f52d973ba44b28a7742006b43",
            "http://textures.minecraft.net/texture/f45c99c80d0345c4be3fc3a2f0d05a3e23a5c4bf7e991568eee64a6806f048c0",
            "http://textures.minecraft.net/texture/b0e4aa6f5455e321059e202abc9d9e23675663070e92a079e8cb544f7be4c755",
            "http://textures.minecraft.net/texture/5007c4a3e3b8d31b94a95173bc2aea4b718c150f0166f0c964ca9e04be664a22",
            "http://textures.minecraft.net/texture/70c6c3a913af9293d6372b0bd4c2de2cc6d3b6e473e3fa0fff034741a612829d",
        )
        val LIBRARIAN = VillagerSkin(
            "http://textures.minecraft.net/texture/e66a53fc707ce1ff88a576ef40200ce8d49fae4acad1e3b3789c7d1cc1cc541a",
            "http://textures.minecraft.net/texture/ebff5901b97efef922555325e910a6d35cc46967ff8a7c2a0e5753af23ddcff2",
            "http://textures.minecraft.net/texture/71f714133ce78d1181c4d5d3e53711ece10c4c9a28201188ee1a6f35cc0fa3ca",
            "http://textures.minecraft.net/texture/1806f9767f087e3e4c09ad012bfd063d013ba4c3169fbb0efd7538e28d7d83d",
            "http://textures.minecraft.net/texture/e7473596a1cb40cf1b3ec5f46f2f9d590d5e78d0507680a7b9bc4304587da0c9",
            "http://textures.minecraft.net/texture/74f04eb20cd0b82aaef2520aed9867c3cca247a4cd975a12ea50df03a7176241",
        )
        val MASON = VillagerSkin(
            "http://textures.minecraft.net/texture/2c02c3ffd5705ab488b305d57ff0168e26de70fd3f739e839661ab947dff37b1",
            "http://textures.minecraft.net/texture/4c4d7ea038187770cc2e4817c9209e19b74f5d288ed633281ecccaf5c8ebc767",
            "http://textures.minecraft.net/texture/d00364c98af059ae6d581fca6038bee14b869998fb3aa382b3c4775d54e8481f",
            "http://textures.minecraft.net/texture/f6a5a4b492cf3861d3044a911e1364dadf7a2be41fb2f9a5c619de5cc9a5af00",
            "http://textures.minecraft.net/texture/4b17427d4e9d89fa1e2cb297cd146ed2fdb49721a0eabf048e7e7d24c73fcda5",
            "http://textures.minecraft.net/texture/621ec612f8f78984a08f8290bd3f1c1892b4f7827b524dbab7eaacc9dd9e22b2",
        )
        val NITWIT = VillagerSkin(
            "http://textures.minecraft.net/texture/35e799dbfaf98287dfbafce970612c8f075168977aacc30989d34a4a5fcdf429",
            "http://textures.minecraft.net/texture/787cb532f85b33b3b141020aa051c35dc8e9cc0ae13ea258f1dfe5e0445f3bcc",
            "http://textures.minecraft.net/texture/522568354f535b094035cee868a4f7985788bd5755b80c0dc8dfc443969faea7",
            "http://textures.minecraft.net/texture/20c641e3d3764ed1c1f1907c4334e2b1303e2152b13d1eb0c605763f97fb258a",
            "http://textures.minecraft.net/texture/51df1fd0f9937c631c6ec26e4b4ec61dd6ba1dfb2ba078f46379d993ee88d735",
            "http://textures.minecraft.net/texture/37d2147ac47a1c9588557f92f83109262a93ecf32170aa8b62056e1629f790a2",
        )
        val SHEPHERD = VillagerSkin(
            "http://textures.minecraft.net/texture/19e04a752596f939f581930414561b175454d45a0506501e7d2488295a5d5de",
            "http://textures.minecraft.net/texture/ce36c366aeb30385cff151a8cf90bac5a8979a55bc2a808875e233d0f81b24a9",
            "http://textures.minecraft.net/texture/431c0f4603b51eb6d8892f8ec0b520979041d671b8d378c8b26d097b8f7e1327",
            "http://textures.minecraft.net/texture/d1644a552dd06f797413c002d41da52904a7bcfd744c5d2c1fe348d9f66cbbeb",
            "http://textures.minecraft.net/texture/69cf18b0447ff1dfcfba9e4c4ac7f6e26a986352dd1878c6a1e00d0f5dd6211d",
            "http://textures.minecraft.net/texture/45771b738349d1de0e01e894ae401686f598ca19a2088b095149fde9b76a4377",
        )
        val TOOLSMITH = VillagerSkin(
            "http://textures.minecraft.net/texture/7dfa07fd1244eb8945f4ededd00426750b77ef5dfbaf03ed775633459ece415a",
            "http://textures.minecraft.net/texture/29d904fdae68fb120e9ae0f3537460f2a7c1de9159ab3f2b44c844048febabeb",
            "http://textures.minecraft.net/texture/7d586f55be429db689c070c47aa9b1284cd51da493768559d7132df8c8916aed",
            "http://textures.minecraft.net/texture/fe7db3a5cb5dd6811fa87e2d113aa6057c669078dd62ff28b377f168277d95ce",
            "http://textures.minecraft.net/texture/ad074b26b09c67feefea4e0245f63306e45cb935e98dbfaa3020eb40c7069719",
            "http://textures.minecraft.net/texture/1a851258491341c00149a9c92de1acde665b131c8a74c9ffe0cb1e3a5ad9749",
        )
        val WEAPONSMITH = VillagerSkin(
            "http://textures.minecraft.net/texture/5e409b958bc4fe045e95d325e6e97a533137e33fec7042ac027b30bb693a9d42",
            "http://textures.minecraft.net/texture/ebba69f6ee3e128bc2feec78c247b2a2f00c3aea11d8906c728de92c60a542ed",
            "http://textures.minecraft.net/texture/c1beaa099c823332e7780a32110f5b0bfc2546e53fde8e206817325894018f3",
            "http://textures.minecraft.net/texture/2844e3ffcc17d4ab0d0eebb6bfdb9603e2f7a095d700028c9db275ae1a95e7f2",
            "http://textures.minecraft.net/texture/4625c64beded1875b8cd9fdf810f16430e74197371572024b7307f26637573f6",
            "http://textures.minecraft.net/texture/8e02febb4c52db1fb9e1e5c852a4e72d8dfe6c4c055a4649abf3d357d233fc1b",
        )

        override fun getSkin(entity: Villager): String = when (entity.villagerData.profession()) {
            in VillagerProfession.ARMORER -> ARMORER
            in VillagerProfession.BUTCHER -> BUTCHER
            in VillagerProfession.CARTOGRAPHER -> CARTOGRAPHER
            in VillagerProfession.CLERIC -> CLERIC
            in VillagerProfession.FARMER -> FARMER
            in VillagerProfession.FISHERMAN -> FISHERMAN
            in VillagerProfession.FLETCHER -> FLETCHER
            in VillagerProfession.LEATHERWORKER -> LEATHERWORKER
            in VillagerProfession.LIBRARIAN -> LIBRARIAN
            in VillagerProfession.MASON -> MASON
            in VillagerProfession.NITWIT -> NITWIT
            in VillagerProfession.SHEPHERD -> SHEPHERD
            in VillagerProfession.TOOLSMITH -> TOOLSMITH
            in VillagerProfession.WEAPONSMITH -> WEAPONSMITH
            else -> NONE
        }.getSkin(entity)
    }
}
