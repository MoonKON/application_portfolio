using System.Collections;
using System.Collections.Generic;
using System.Xml.Serialization;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;
using UnityEngine.Video;

public class StartMenu : MonoBehaviour
{
    public VideoPlayer videoPlayer; // 拖动 Video Player GameObject 到这里
    public RawImage rawImage;

    private void Start()
    {
        // 将 Raw Image 的透明度设置为 0
        Color rawImageColor = rawImage.color;
        rawImageColor.a = 0f;
        rawImage.color = rawImageColor;

        // 将 Video Player 的图像渲染到 Raw Image
        videoPlayer.targetTexture = new RenderTexture(1920, 1080, 0); // 创建一个渲染纹理
        rawImage.texture = videoPlayer.targetTexture; // 将 Raw Image 的纹理设置为渲染纹理
    }


    public void StartGame()
    {
        Debug.Log("StartGame");
        StartCoroutine(StartVideo());
    }

    private IEnumerator StartVideo()
    {
        Debug.Log("Start raw image");
        Color rawImageColor = rawImage.color;
        rawImageColor.a = 0f;
        rawImage.color = rawImageColor;


        float duration = 1f;
        for (float t = 0; t <= duration; t += Time.deltaTime)
        {
            rawImageColor.a = Mathf.Lerp(0f, 1f, t / duration); 
            rawImage.color = rawImageColor;
            yield return null;
        }


        yield return new WaitForSeconds(1f);

        videoPlayer.Play();

        // 等待视频播放完成
        while (videoPlayer.isPlaying)
        {
            yield return null; // 等待视频播放
        }

        // 等待 1 秒
        yield return new WaitForSeconds(38f);

        // 切换到下一场景
        SceneManager.LoadScene(SceneManager.GetActiveScene().buildIndex + 1);

    }
}
