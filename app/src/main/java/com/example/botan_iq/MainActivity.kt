package com.example.botan_iq

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.botan_iq.databinding.ActivityMainBinding
import com.example.botan_iq.ml.ModelUnquant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var bitmap: Bitmap
    lateinit var imgview: ImageView
    lateinit var text_view: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var select: Button = findViewById(R.id.button)
        imgview = findViewById(R.id.imageView2)
        var tv: TextView = findViewById(R.id.textView3)
        val fileName = "label.txt"
       // var inputString = application.assets.open(fileName).bufferedReader().use{it.readText()}
        var inputString = application.assets.open(fileName).bufferedReader().readLines()
        //var townList = inputString.split("\n")

        var imageProcessor = ImageProcessor.Builder()
            //.add(NormalizeOp(0.0f,255.0f))
            .add(ResizeOp(224,224,ResizeOp.ResizeMethod.BILINEAR))
            //.add(TransformToGrayscaleOp())
            .build()

        select.setOnClickListener(View.OnClickListener{
            var intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(intent,100)
            //intent.putExtra("requestCode",100)
        })

        var predict: Button = findViewById(R.id.button2)
        predict.setOnClickListener(View.OnClickListener {
            var resized: Bitmap = Bitmap.createScaledBitmap(bitmap,224,224,true)
            /*val model = MobilenetV110224Quant.newInstance(this)

// Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)

            var tbuffer = TensorImage.fromBitmap(resized)
            var byteBuffer = tbuffer.buffer

            inputFeature0.loadBuffer(byteBuffer)

// Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            var max = getMax(outputFeature0.floatArray)

            tv.setText(townList[max])

// Releases model resources if no longer used.
            model.close()*/
            var tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)

            tensorImage = imageProcessor.process(tensorImage)

            val model = ModelUnquant.newInstance(this)

// Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            var tbuffer = TensorImage.fromBitmap(resized)
            var byteBuffer = tbuffer.buffer
            //inputFeature0.loadBuffer(byteBuffer)
            inputFeature0.loadBuffer(tensorImage.buffer)

// Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

            var maxIdx = 0
            outputFeature0.forEachIndexed{index, fl ->
            if(outputFeature0[maxIdx] < fl){
                maxIdx = index
            }
            }
            tv.setText(inputString[maxIdx])
            binding.imgName.setText(inputString[maxIdx])
            binding.description.setText(
                when(inputString[maxIdx]){
                    "13 Ail(Garlic)"->{"Garlic, scientifically known as Allium sativum (Ail), has been used for its medicinal properties for thousands of years.\n" +
                            "•\tCardiovascular health support\n" +
                            "•\tImmune system boost\n" +
                            "•\tAntioxidant properties\n" +
                            "•\tAnti-inflammatory effects\n" +
                            "•\tPotential anticancer properties\n" +
                            "•\tDetoxification support\n" +
                            "•\tImproved digestion\n" +
                            "•\tBlood sugar regulation\n" +
                            "•\tSkin health benefits\n"
                            }
                    "11 Muguet"->{"Muguet in French also known as the “Lily of the valley”, is primarily admired for its fragrant flowers and is not commonly used for medicinal purposes due to its toxicity. Nonetheless, here are some points highlighting potential medicinal benefits without descriptions:\n" +
                            "\n" +
                            "•\tTraditional use in herbal medicine\n" +
                            "•\tCardiotonic properties\n" +
                            "•\tDiuretic effects\n" +
                            "•\tLimited use in treating heart conditions\n" +
                            "•\tHistorically used to relieve inflammation"}
                    "0 Ibéris amer"->{"Ibéris Amer (American False Candytuft) is a flowering plant known for its delicate, white-petaled flowers and its scientific name, Iberis amara. It is a member of the Brassicaceae family and is native to Europe and Asia. Below is a brief description of Ibéris Amer along with some potential health benefits:" +
                            "•\tDigestive Aid\n" +
                            "•\tAnti-Inflammatory\n" +
                            "•\tAntioxidant\n" +
                            "•\tRespiratory Health\n" +
                            "•\tSupport skin health"}
                    "4 Carotte"->{"Carrot, (Carotte), herbaceous, generally biennial plant of the Apiaceae family that produces an edible taproot. \n" +
                            "•\tRich in vitamin A\n" +
                            "•\tGood for eye health\n" +
                            "•\tAntioxidant properties\n" +
                            "•\tSupports skin health\n" +
                            "•\tAids digestion\n" +
                            "•\tLowers the risk of chronic diseases\n" +
                            "•\tSupports weight management\n" +
                            "•\tPromotes heart health\n"}
                    "17 Ginseng"->{"Ginseng, known as Ginger, used in traditional medicine in China, India and Japan for centuries, and as a dietary supplement, research shows that ginger may be helpful for mild nausea, vomiting associated with pregnancy popular herbal remedy known for its various medicinal benefits:\n" +
                            "•\tEnergy booster\n" +
                            "•\tStress reduction\n" +
                            "•\tCognitive function enhancement\n" +
                            "•\tImmune system support\n" +
                            "•\tAnti-inflammatory effects\n" +
                            "•\tBlood sugar regulation\n"}
                    "10 Fenugrec"->{"Fenugrec (Fenugreek) is a clover-like herb native to the Mediterranean region, southern Europe, and western Asia , has miraculous medicinal benefits:\n" +
                            "•\tBlood sugar regulation\n" +
                            "•\tCholesterol management\n" +
                            "•\tDigestive support\n" +
                            "•\tAnti-inflammatory effects\n" +
                            "•\tLactation enhancement\n" +
                            "•\tAppetite control\n" +
                            "•\tWeight management\n" +
                            "•\tSkin health benefits\n" +
                            "•\tPotential testosterone boost\n"}


                    else -> {"For more INFO visit our website \nBotan-IQ.com"}
                }
            )

           // var max = getMax(outputFeature0.floatArray)

            //tv.setText(inputString[max])
// Releases model resources if no longer used.
            model.close()

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        imgview.setImageURI(data?.data)

        var uri: Uri?= data?.data
        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
        imgview.setImageBitmap(bitmap)
    }

    fun getMax(arr:FloatArray):Int{

        var ind = 0
        var min = 0.0f
        for(i in 0..17){
            if(arr[i]>min){
                ind = i
                min = arr[i]
            }
        }
        return ind
    }
}