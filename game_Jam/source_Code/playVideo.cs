using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.Video;

public class playVideo : MonoBehaviour
{
    [SerializeField] private VideoPlayer videoPlayer;
    public IEnumerator Play()
    {
        Debug.Log("Video Play1!!");
        videoPlayer.Play();
        while (videoPlayer.isPlaying)
        {
            yield return null; // 等待视频播放
        }
        yield return new WaitForSeconds(1f);

        // 切换到下一场景
        SceneManager.LoadScene(SceneManager.GetActiveScene().buildIndex + 1);
    }
}
